package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.web.service.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("user") UserInformationDto user) {
        return "account/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid UserInformationDto user,
                           BindingResult bindingResult,
                           HttpServletRequest request) {

        loginService.register(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "account/register";
        }

        return "redirect:/";
    }
}
