package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;

import java.util.List;

@Service
@AllArgsConstructor
public class CheckoutService {

    private final OrderService orderService;
    private final CartService cartService;


    /**
     * Posts an order with products from users cart
     * @param userCredentials users credentials
     * @param orderDto order information - delivery address & payment method
     */
    public void postOrder(UserCredentials userCredentials, OrderDto orderDto) {
        orderService.postOrder(userCredentials, orderDto);
    }


    /**
     * @param userCredentials users credentials
     * @return cart of a user
     */
    public Cart getCartLogged(UserCredentials userCredentials) {
        return cartService.getCartLogged(userCredentials);
    }


    /**
     * @return if cart is empty
     */
    public boolean isCartEmpty(Cart cart) {
        return cart.getProductList().size() == 0;
    }


    /**
     * It verifies if quantities of products in cart are correct - between 1 and available quantity
     * @param cart - cart to be verified
     */
    public void verifyCart(Cart cart) {
        cartService.adjustProductsInCartQuantities(cart); // it changes quantities if needed + sets just changed to true
    }


    /**
     * @return all available payment methods
     */
    public PaymentMethod[] getPaymentMethods() {
        return PaymentMethod.values();
    }
}
