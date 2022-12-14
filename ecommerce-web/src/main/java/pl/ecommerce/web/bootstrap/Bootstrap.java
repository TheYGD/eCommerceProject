package pl.ecommerce.web.bootstrap;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.ecommerce.admin.domain.Account;
import pl.ecommerce.admin.domain.Role;
import pl.ecommerce.admin.repository.AccountRepository;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * This class bootstraps the application with some data to help with development
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("default")
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final CartRepository cartRepository;
    private final AccountRepository accountRepository;
    private final PseudoEnumRepository pseudoEnumRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final ProductAttributeRepository productAttributeRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.count() == 0) {
            bootstrap1();
            createUser();
            createAdmin();

            log.debug("Bootstrapping done!");
        }
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


        Category rootCategory = new Category("Wszystko", "Wszystko");
        rootCategory.setOrderId(1);
        categoryRepository.save(rootCategory);

        Category category1 = new Category("Samochody", "Znajduj?? si?? tutaj samochody");
        category1.setOrderId(2);
        category1.setParent(rootCategory);
        categoryRepository.save(category1);

        CategoryAttribute attr1 = new CategoryAttribute(category1, "Rok produkcji", true, null);
        PseudoEnum pseudoEnum = new PseudoEnum();
        PseudoEnumValue val1 = new PseudoEnumValue(1, pseudoEnum, "Osobowe");
        PseudoEnumValue val2 = new PseudoEnumValue(2, pseudoEnum, "Ci????arowe");
        pseudoEnum.setValues(List.of(val1, val2));
        CategoryAttribute attr2 = new CategoryAttribute(category1, "Typ", false, pseudoEnum);

        pseudoEnumRepository.save(pseudoEnum);
        categoryAttributeRepository.saveAll(List.of(attr1, attr2));


        Product product1 = productRepository.save( new Product("Audi1", "szybkie 2 audi", category1, seller1,
                BigDecimal.valueOf(100000), null) );
        AvailableProduct availableProduct1 = new AvailableProduct(product1, 1, 0, false);
        availableProduct1.setId(product1.getId());
        product1.setAvailableProduct(availableProduct1);
        ProductAttribute pAttr1 = new ProductAttribute(attr1, product1, BigDecimal.valueOf(2008));
        ProductAttribute pAttr2 = new ProductAttribute(attr2, product1, BigDecimal.valueOf(1));

        Product product2 = productRepository.save( new Product("BMW1", "szybkie 2 bmw", category1, seller1,
                BigDecimal.valueOf(120000), null) );
        AvailableProduct availableProduct2 = new AvailableProduct(product2, 2, 0, false);
        availableProduct2.setId(product2.getId());
        product2.setAvailableProduct(availableProduct2);
        ProductAttribute pAttr3 = new ProductAttribute(attr1, product2, BigDecimal.valueOf(2015));
        ProductAttribute pAttr4 = new ProductAttribute(attr2, product2, BigDecimal.valueOf(1));


        Product product3 = productRepository.save( new Product("Mercedes1", "szybki 2 mercedes", category1, seller1,
                BigDecimal.valueOf(150000), null) );
        AvailableProduct availableProduct3 = new AvailableProduct(product3, 3, 0, false);
        availableProduct3.setId(product3.getId());
        product3.setAvailableProduct(availableProduct3);
        ProductAttribute pAttr5 = new ProductAttribute(attr1, product3, BigDecimal.valueOf(2012));
        ProductAttribute pAttr6 = new ProductAttribute(attr2, product3, BigDecimal.valueOf(1));

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productAttributeRepository.saveAll(List.of(pAttr1, pAttr2, pAttr3, pAttr4, pAttr5, pAttr6));
    }


    private void createAdmin() {
        Account account = new Account();

        account.setUsername("admin");
        account.setPassword( passwordEncoder.encode("admin") );
        account.setRole(Role.ADMIN);
        account.setFirstName("Artur");
        account.setFirstName("Arturowicz");
        account.setLocked(false);

        accountRepository.save(account);
    }
}
