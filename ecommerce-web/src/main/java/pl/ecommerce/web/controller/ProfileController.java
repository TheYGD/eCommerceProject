package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.web.service.ProfileService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping
    public String profilePage() {
        return "profiles/index";
    }


    @GetMapping("/sold")
    public String soldProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<SoldProductsGroup> soldProductsGroupList = profileService.getSoldProductsGroupList(userCredentials);

        model.addAttribute("soldProductsGroupList", soldProductsGroupList);

        return "profiles/sold-products";
    }


    @GetMapping("/ordered")
    public String orderedProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<Order> orderList = profileService.getOrderedProducts(userCredentials);

        model.addAttribute("orderList", orderList);

        return "profiles/ordered-products";
    }


    @GetMapping("/your-products")
    public String ownProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<Product> productList = profileService.getOwnProducts(userCredentials);

        model.addAttribute("productList", productList);

        return "profiles/own-products";
    }

}
