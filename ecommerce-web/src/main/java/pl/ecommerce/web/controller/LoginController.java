package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.ecommerce.data.dto.UserRegisterDto;
import pl.ecommerce.web.service.LoginService;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("user") UserRegisterDto userRegisterDto) {
        return "account/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid UserRegisterDto userRegisterDto,
                           BindingResult bindingResult) {

        loginService.register(userRegisterDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "account/register";
        }

        return "redirect:/";
    }
}
