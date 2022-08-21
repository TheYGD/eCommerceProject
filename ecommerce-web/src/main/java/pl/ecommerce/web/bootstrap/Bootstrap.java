package pl.ecommerce.web.bootstrap;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;

/**
 * This class bootstraps the application with some data to help with development
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final CartRepository cartRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bootstrap1();
        createUser();

        log.debug("Bootstrapping done!");
    }


    @Transactional
    public void createUser() { // easy login
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername(" ");
        userCredentials.setPassword(passwordEncoder.encode(" "));
        userCredentials.setEmail("");
        userCredentials.setLocked(false);

        User user = new User();
        Cart cart = cartRepository.save(new Cart());
        cart.setOwner(user);

        user.setCredentials(userCredentials);
        userCredentials.setUserAccount(user);
        user.setCart(cart);

        userRepository.save(user);
        userCredentialsRepository.save(userCredentials);
    }

    @Transactional
    public void bootstrap1() {

        User seller1 = new User();
        UserCredentials sellerCredentials = new UserCredentials();
        sellerCredentials.setUsername("sprzedawca1");
        seller1.setCredentials(sellerCredentials);

        userRepository.save(seller1);
        userCredentialsRepository.save(new UserCredentials());

        Category category1 = new Category("Samochody", "Znajdują się tutaj samochody");
        Product product1 = new Product("Audi1", "szybkie 2 audi", category1, seller1, 1,
                BigDecimal.valueOf(100000), null);
        category1.getProducts().add(product1);

        Product product2 = new Product("BMW1", "szybkie 2 bmw", category1, seller1, 1,
                BigDecimal.valueOf(120000), null);
        category1.getProducts().add(product2);


        Product product3 = new Product("Mercedes1", "szybki 2 mercedes", category1, seller1, 1,
                BigDecimal.valueOf(150000), null);
        category1.getProducts().add(product3);

//        userRepository.save(seller1);
        categoryRepository.save(category1);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
    }
}
