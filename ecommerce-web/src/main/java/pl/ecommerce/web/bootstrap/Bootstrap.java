package pl.ecommerce.web.bootstrap;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.ecommerce.data.entity.Category;
import pl.ecommerce.data.entity.Product;
import pl.ecommerce.data.entity.User;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserCredentialsRepository;
import pl.ecommerce.repository.UserRepository;

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

    private final PasswordEncoder passwordEncoder;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bootstrap1();
        createUser();

        log.debug("Bootstrapping done!");
    }


    private void createUser() { // easy login
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername(" ");
        userCredentials.setPassword(passwordEncoder.encode(" "));
        userCredentials.setEmail("");
        userCredentials.setLocked(false);

        User user = new User();

        user.setCredentials(userCredentials);
        userCredentials.setUserAccount(user);

        userRepository.save(user);
        userCredentialsRepository.save(userCredentials);
    }

    @Transactional
    public void bootstrap1() {
        Category category1 = new Category("Samochody", "Znajdują się tutaj samochody");
        Product product1 = new Product("Audi1", "szybkie audi", category1, null, 1,
                BigDecimal.valueOf(100000), null);
        category1.getProducts().add(product1);

        productRepository.save(product1);
        categoryRepository.save(category1);
    }
}
