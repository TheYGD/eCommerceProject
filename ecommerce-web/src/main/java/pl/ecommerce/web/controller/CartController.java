package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.data.entity.Cart;
import pl.ecommerce.data.entity.Product;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.web.service.CartService;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String showCart(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {
        Cart cart = cartService.findByUserCredentials(userCredentials);
        model.addAttribute("cart", cart);

        return "cart/show";
    }
}
