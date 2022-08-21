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

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final SoldProductRepository soldProductRepository;
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

        List<SoldProduct> products = cart.getProductList().stream()
                .map( productInCart -> soldProductRepository.save(
                        new SoldProduct(productInCart.getProductWithQuantity(), order)))
                .toList();

        order.setProductList( products );
        orderRepository.save(order);

        productInCartRepository.deleteAll(cart.getProductList());

        cart.setProductList(new LinkedList<>());
        cartRepository.save(cart);

        return order.getId();
    }
}
