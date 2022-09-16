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
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping("/own")
    public String profilePage() {
        return "profile/show";
    }


    // TODO: make actual profile page
    // TODO: make actual profile page
    // TODO: make actual profile page
    @GetMapping("/{id}")
    public String sellersProfile(@PathVariable Long id) {

        return "redirect:/sellers/" + id;
    }


    @GetMapping("/own/sold")
    public String soldProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<Order> orderList = profileService.getSoldProductsList(userCredentials);

        model.addAttribute("orderList", orderList);

        return "profile/sold-products";
    }


    @GetMapping("/own/ordered")
    public String orderedProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<Order> orderList = profileService.getOrders(userCredentials);

        model.addAttribute("orderList", orderList);

        return "profile/ordered-products";
    }


    @GetMapping("/own/your-products")
    public String ownProductsPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model,
                                  @RequestParam(defaultValue = "1") int pageNr,
                                  @RequestParam(defaultValue = "0") int sortOption) {

        Page<AvailableProduct> productPage = profileService.getOwnProducts(userCredentials, pageNr, sortOption);

        model.addAttribute("productPage", productPage);

        return "profile/own-products";
    }


    @GetMapping("/own/user-information")
    @ResponseBody
    public UserInformationDto getUserInformation(@AuthenticationPrincipal UserCredentials userCredentials) {
        return profileService.getUserInformation(userCredentials);
    }


    @PutMapping("/own/user-information/change")
    @ResponseBody
    public StringResponse updateUserInformation(@AuthenticationPrincipal UserCredentials userCredentials,
                                                @ModelAttribute @Valid UserInformationDto userInformationDto,
                                                BindingResult bindingResult) {

        profileService.updateUserInformation(userCredentials, userInformationDto, bindingResult);
        return new StringResponse("Information updated.");
    }


    @PutMapping("/own/password/change")
    @ResponseBody
    public StringResponse changePassword(@AuthenticationPrincipal UserCredentials userCredentials,
                                                @ModelAttribute @Valid PasswordChangeDto passwordChangeDto,
                                                BindingResult bindingResult) {

        profileService.changePassword(userCredentials, passwordChangeDto, bindingResult);
        return new StringResponse("Password changed successfully.");
    }
}
