package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.domain.PaymentMethod;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.data.domain.Cart;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.web.service.CheckoutService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;


    @GetMapping
    public String checkoutPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        Cart cart = checkoutService.getCartLogged(userCredentials);
        checkoutService.verifyCart(cart);

        if (cart.isJustChangedCart() || checkoutService.isCartEmpty(cart)) {
            return "redirect:/cart";
        }

        PaymentMethod[] paymentMethods = checkoutService.getPaymentMethods();

        model.addAttribute("cart", cart);
        model.addAttribute("orderDto", new OrderDto());
        model.addAttribute("paymentMethods", paymentMethods);

        return "checkout/show";
    }


    @PostMapping
    public String postOrder(@AuthenticationPrincipal UserCredentials userCredentials,
                            @ModelAttribute @Valid OrderDto orderDto, BindingResult bindingResult) {

        Cart cart = checkoutService.getCartLogged(userCredentials);
        checkoutService.verifyCart(cart);
        if (cart.isJustChangedCart()) {
            return "redirect:/cart";
        }

        if (bindingResult.hasErrors()) {
            return "checkout/show";
        }

        checkoutService.postOrder(userCredentials, orderDto);
        return "redirect:/profiles/own/ordered";
    }
}
