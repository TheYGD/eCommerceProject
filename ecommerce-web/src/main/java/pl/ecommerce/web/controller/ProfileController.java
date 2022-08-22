package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.SoldProductsGroup;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
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

}
