package pl.ecommerce.web.security;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.web.service.CartService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final String CART_COOKIE = "CART_HASH";

    private final CartService cartService;


    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.
                        currentRequestAttributes()).
                        getRequest();

        if (request.getCookies() == null) {
            return;
        }

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(CART_COOKIE))
                .findFirst()
                .ifPresent(cookie -> {
                    UserCredentials userCredentials = (UserCredentials) event.getAuthentication().getPrincipal();
                    cartService.mergeCartsAfterLogin(userCredentials, Long.valueOf(cookie.getValue()));

                    HttpServletResponse response =
                            ((ServletRequestAttributes) RequestContextHolder.
                                    currentRequestAttributes()).
                                    getResponse();

                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                });

    }
}
