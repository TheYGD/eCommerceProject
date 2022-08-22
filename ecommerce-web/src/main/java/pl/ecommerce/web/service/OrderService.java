package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.data.mapper.OrderMapper;
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
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final SoldProductRepository soldProductRepository;
    private final SoldProductsGroupRepository soldProductsGroupRepository;
    private final AddressRepository addressRepository;
    private final ProductInCartRepository productInCartRepository;
    private final CartRepository cartRepository;


    public Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow( () -> {
//                    log.error("Order not found!");
                    return new ItemNotFoundException("Order not found!");
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
                        new SoldProduct(productInCart.getProductWithQuantity().getProduct(),
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
}
