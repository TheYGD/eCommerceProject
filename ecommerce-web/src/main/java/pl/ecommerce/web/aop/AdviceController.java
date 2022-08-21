package pl.ecommerce.web.aop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.web.service.CartService;


@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class AdviceController {

    private CartService cartService;


    @ModelAttribute("username")
    public String attachUserToModel(@AuthenticationPrincipal UserCredentials userCredentials) {
        return userCredentials != null ? userCredentials.getUsername() : null;
    }
}
