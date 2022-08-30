package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.data.mapper.OrderMapper;
import pl.ecommerce.exceptions.ForbiddenException;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.exceptions.NotEnoughProductQuantityException;
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    private final CartService cartService;
    private final MessageService messageService;
    private final OrderRepository orderRepository;
    private final ProductInCartRepository productInCartRepository;
    private final AvailableProductRepository availableProductRepository;
    private final ExpiredProductRepository expiredProductRepository;
    private final CartRepository cartRepository;


    public Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow( () -> {
                    String message = "Order with id=%d not found!".formatted(id);
//                    log.warn(message);
                    return new ItemNotFoundException(message);
                });
    }


    @Transactional
    public void postOrder(UserCredentials userCredentials, OrderDto orderDto) {

        User user = userCredentials.getUserAccount();
        Cart cart = cartService.getCartLogged(userCredentials);

        Address address = OrderMapper.INSTANCE.DtoToEntityAddress(orderDto);
        PaymentMethod paymentMethod = PaymentMethod.valueOf( orderDto.getPaymentOption() );
        LocalDateTime orderDate = LocalDateTime.now();

        List<Order> orderList = cart.getProductList().stream()
                .map( productInCart -> {
                    Product product = changeAvailableQuantity(productInCart);
                    return new SoldProduct( product,
                            productInCart.getQuantity() );
                })
                .collect(Collectors.groupingBy( soldProduct -> soldProduct.getProduct().getSeller() ))
                .values().stream()
                .map( soldProducts -> new Order(soldProducts, address, user,
                        soldProducts.get(0).getProduct().getSeller(), orderDate, paymentMethod) )
                .toList();

        orderList.forEach( order ->
            order.getSoldProductsList().forEach( product -> product.setOrder(order) ));

        orderRepository.saveAll( orderList );

        productInCartRepository.deleteAll(cart.getProductList());

        cart.setProductList(new LinkedList<>());
        cartRepository.save(cart);
    }

    private Product changeAvailableQuantity(ProductInCart productInCart) {
        AvailableProduct availableProduct = productInCart.getAvailableProduct();

        if (availableProduct.getQuantity() < productInCart.getQuantity()) {
            throw new NotEnoughProductQuantityException(
                    "One or more products in your cart are not available in the desired quantity.");
        }

        availableProduct.setQuantity( availableProduct.getQuantity() - productInCart.getQuantity() );
        availableProduct.setSoldQuantity( availableProduct.getSoldQuantity() + productInCart.getQuantity() );

        if (availableProduct.getQuantity() == 0) {
            ExpiredProduct expiredProduct = ExpiredProduct.builder()
                    .product(availableProduct.getProduct())
                    .quantity(availableProduct.getQuantity())
                    .soldQuantity(availableProduct.getSoldQuantity()).build();
            expiredProduct.setId( availableProduct.getId() );

            availableProduct.getProduct().setAvailableProduct(null);

            productInCartRepository.deleteAllByProduct(availableProduct);

            expiredProductRepository.save(expiredProduct);
            availableProductRepository.delete(availableProduct);
        }

        else {
            availableProductRepository.save(availableProduct);
        }

        return availableProduct.getProduct();
    }

    public Long askAboutOrder(UserCredentials userCredentials, Long orderId, Map<String, String> formData) {

        if (!formData.containsKey("titleId") || !formData.containsKey("content")) {
            throw new InvalidArgumentException("Error! Try again later.");
        }

        int titleId = Integer.parseInt(formData.get("titleId"));
        String content = formData.get("content");

        MessageCause messageCause = MessageCause.findById(titleId)
                .orElseThrow( () -> new ItemNotFoundException("Message cause not found!") );
        if (!messageCause.isOrderCause()) {
            log.warn("Invalid message cause id=%d chose for message about order.".formatted(titleId));
            throw new InvalidArgumentException("Error! Try again later");
        }

        Order order = findOrder(orderId);
        orderBelongsToUser(order, userCredentials);

        String link = "/orders/" + orderId;
        String linkName = "Order #" + order.getId();

        return messageService.createChat(userCredentials.getUserAccount(), order.getSeller(),
                messageCause, content, link, linkName );                                 //todo change to seller ***********************************************
    }


    public List<MessageCause> getOrderMessageTitles() {
        return MessageCause.getCausesForOrder();
    }

    public void orderBelongsToUser(Order order, UserCredentials userCredentials) {
        if (!order.getBuyer().equals(userCredentials.getUserAccount())) {
            log.warn("User with id=%d tried to create chat about order with id=%d."
                    .formatted(userCredentials.getUserAccount().getId()), order.getId());
            throw new ForbiddenException("Access denied");
        }
    }
}
