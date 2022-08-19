package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.entity.Cart;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.data.other.StringResponse;
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


    @PostMapping("/change/{id}")
    @ResponseBody
    public StringResponse changeProductsQuantity(@AuthenticationPrincipal UserCredentials userCredentials,
                                         @PathVariable Long id, @RequestParam Integer quantity) {
        cartService.changeProductsQuantity(userCredentials, id, quantity);

        return new StringResponse("Quantity changed");
    }


    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public StringResponse removeProductFromCart(@AuthenticationPrincipal UserCredentials userCredentials, @PathVariable Long id) {
        cartService.removeProduct(userCredentials, id);

        return new StringResponse("Product deleted");
    }


    @GetMapping("/size")
    @ResponseBody
    public StringResponse getCartSize(@AuthenticationPrincipal UserCredentials userCredentials) {
        int size = cartService.getCartByUserCredentials(userCredentials).getProductList().size();

        return new StringResponse(String.valueOf(size));
    }
}
