package pl.ecommerce.web.bootstrap;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.ecommerce.admin.domain.Account;
import pl.ecommerce.admin.domain.Role;
import pl.ecommerce.admin.repository.AccountRepository;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;

/**
 * This is a bootstrap class for tests
 */
@Component
@RequiredArgsConstructor
@Profile("test")
public class BootstrapTest implements ApplicationListener<ContextRefreshedEvent> {
    private final CategoryRepository categoryRepository;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (categoryRepository.count() == 0) {
            createRootCategory();
        }
    }


    @Transactional
    public void createRootCategory() {
        Category rootCategory = new Category("Wszystko", "Wszystko");
        rootCategory.setOrderId(1);
        categoryRepository.save(rootCategory);
    }
}
