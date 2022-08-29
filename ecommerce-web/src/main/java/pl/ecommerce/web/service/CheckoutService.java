package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.data.mapper.OrderMapper;
import pl.ecommerce.repository.CartRepository;
import pl.ecommerce.repository.OrderRepository;
import pl.ecommerce.repository.SoldProductRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CheckoutService {

    private final OrderService orderService;
    private final CartService cartService;


    public void postOrder(UserCredentials userCredentials, OrderDto orderDto) {
        orderService.postOrder(userCredentials, orderDto);
    }

    public Cart getCartLogged(UserCredentials userCredentials) {
        return cartService.getCartLogged(userCredentials);
    }

    public boolean isCartEmpty(Cart cart) {
        return cartService.isCartEmpty(cart);
    }
}
