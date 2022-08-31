package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.PasswordChangeDto;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.data.other.StringResponse;
import pl.ecommerce.web.service.ProfileService;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping()
    public String profilePage() {
        return "profile/show";
    }


    @GetMapping("/sold")
    public String soldProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<Order> orderList = profileService.getSoldProductsList(userCredentials);

        model.addAttribute("orderList", orderList);

        return "profile/sold-products";
    }


    @GetMapping("/ordered")
    public String orderedProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<Order> orderList = profileService.getOrderedProducts(userCredentials);

        model.addAttribute("orderList", orderList);

        return "profile/ordered-products";
    }


    @GetMapping("/your-products")
    public String ownProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model,
                                  @RequestParam(defaultValue = "1") int pageNr,
                                  @RequestParam(defaultValue = "0") int sortOption) {

        Page<AvailableProduct> productPage = profileService.getOwnProducts(userCredentials, pageNr, sortOption);

        model.addAttribute("productPage", productPage);

        return "profile/own-products";
    }


    @GetMapping("/user-information")
    @ResponseBody
    public UserInformationDto getUserInformation(@AuthenticationPrincipal UserCredentials userCredentials) {
        return profileService.getUserInformation(userCredentials);
    }


    @PutMapping("/user-information/change")
    @ResponseBody
    public StringResponse updateUserInformation(@AuthenticationPrincipal UserCredentials userCredentials,
                                                @ModelAttribute @Valid UserInformationDto userInformationDto,
                                                BindingResult bindingResult) {

        profileService.updateUserInformation(userCredentials, userInformationDto, bindingResult);
        return new StringResponse("Information updated.");
    }


    @PutMapping("/password/change")
    @ResponseBody
    public StringResponse changePassword(@AuthenticationPrincipal UserCredentials userCredentials,
                                                @ModelAttribute @Valid PasswordChangeDto passwordChangeDto,
                                                BindingResult bindingResult) {

        profileService.changePassword(userCredentials, passwordChangeDto, bindingResult);
        return new StringResponse("Password changed successfully.");
    }
}
