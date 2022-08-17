package pl.ecommerce.web.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pl.ecommerce.data.entity.UserCredentials;

@Controller
public class IndexController {

    @GetMapping()
    public String indexPage() {
        return "index";
    }
}
