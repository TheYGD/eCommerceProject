package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.domain.Cart;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.other.StringResponse;
import pl.ecommerce.web.service.CartService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String showCart(@AuthenticationPrincipal UserCredentials userCredentials, Model model,
                           HttpServletRequest request, HttpServletResponse response) {
        Cart cart = cartService.getCart(userCredentials, request, response);

        model.addAttribute("cart", cart);
        model.addAttribute("justDeletedProducts", cart.isJustChangedCart());
        cartService.markJustChangedCartAsFalse(cart);

        return "cart/show";
    }


    @PutMapping("/change/{id}")
    @ResponseBody
    public StringResponse changeProductsQuantity(@AuthenticationPrincipal UserCredentials userCredentials,
                                                 @PathVariable Long id, @RequestParam Integer quantity,
                                                 HttpServletRequest request, HttpServletResponse response) {
        cartService.changeProductsQuantity(userCredentials, id, quantity, request, response);

        return new StringResponse("Quantity changed");
    }


    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public StringResponse removeProductFromCart(@AuthenticationPrincipal UserCredentials userCredentials, @PathVariable Long id,
                                                HttpServletRequest request, HttpServletResponse response) {
        cartService.removeProduct(userCredentials, id, request, response);

        return new StringResponse("Product deleted");
    }


    @GetMapping("/size")
    @ResponseBody
    public StringResponse getCartSize(@AuthenticationPrincipal UserCredentials userCredentials,
                                      HttpServletRequest request, HttpServletResponse response) {
        int size = cartService.getCart(userCredentials, request, response).getProductList().size();

        return new StringResponse(String.valueOf(size));
    }
}
