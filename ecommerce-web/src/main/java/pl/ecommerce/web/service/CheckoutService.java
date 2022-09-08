package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;

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
