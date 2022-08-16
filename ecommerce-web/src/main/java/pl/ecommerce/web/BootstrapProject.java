package pl.ecommerce.web;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.ecommerce.domain.entity.Category;
import pl.ecommerce.domain.entity.Product;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.ProductRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

/**
 * This class bootstraps the application with some data to help with development
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapProject implements ApplicationListener<ContextRefreshedEvent> {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bootstrap1();

        log.debug("Bootstrapping done!");
    }

    @Transactional
    public void bootstrap1() {
        Category category1 = new Category("Samochody", "Znajdują się tutaj samochody");
        Product product1 = new Product("Audi1", "szybkie audi", category1, null, 1,
                BigDecimal.valueOf(100000), "");
        category1.getProducts().add(product1);

        productRepository.save(product1);
        categoryRepository.save(category1);
    }
}
