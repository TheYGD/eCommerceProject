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
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
    private final SoldProductRepository soldProductRepository;
    private final SoldProductsGroupRepository soldProductsGroupRepository;
    private final AddressRepository addressRepository;
    private final ProductInCartRepository productInCartRepository;
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
    public Long postOrder(UserCredentials userCredentials, OrderDto orderDto) {

        User user = userCredentials.getUserAccount();
        Cart cart = cartService.getCartLogged(userCredentials);

        Order order = OrderMapper.INSTANCE.DtoToEntity(orderDto);
        order.setPaymentOption( PaymentOption.valueOf(orderDto.getPaymentOption()) );
        order.setBuyer(user);
        order.setDateTime(LocalDateTime.now());
        order.setAddress( addressRepository.save(order.getAddress()) );

        List<SoldProductsGroup> soldProductsGroupList = cart.getProductList().stream()
                .map( productInCart -> soldProductRepository.save(
                        new SoldProduct( new EternalProduct(productInCart.getProductWithQuantity().getProduct()),
                                productInCart.getProductWithQuantity().getQuantity())))
                .collect(Collectors.groupingBy( soldProduct -> soldProduct.getProduct().getSeller() ))
                .values().stream()
                .map( soldProducts -> new SoldProductsGroup(order, soldProducts.get(0).getProduct().getSeller(), soldProducts) )

                .toList();

        soldProductsGroupRepository.saveAll(soldProductsGroupList);
        order.setProductsGroupList( soldProductsGroupList );
        orderRepository.save(order);

        productInCartRepository.deleteAll(cart.getProductList());

        cart.setProductList(new LinkedList<>());
        cartRepository.save(cart);

        return order.getId();
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

        return messageService.createChat(userCredentials.getUserAccount(), order.getProductsGroupList().get(0).getSeller(),
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
