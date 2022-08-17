package pl.ecommerce.web.aop;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.web.controller.IndexController;
import pl.ecommerce.web.controller.ProductController;


@ControllerAdvice(basePackageClasses = {IndexController.class, ProductController.class})
public class AdviceController {

    @ModelAttribute("username")
    public String attachUserToModel(@AuthenticationPrincipal UserCredentials userCredentials) {
        return userCredentials != null ? userCredentials.getUsername() : null;
    }

    @ModelAttribute("cartAmount")
    public Integer attachCartAmount() {
        return (int) (Math.random() * 20);
    }
}
