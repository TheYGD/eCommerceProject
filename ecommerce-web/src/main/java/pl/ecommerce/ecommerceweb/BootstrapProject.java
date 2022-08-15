package pl.ecommerce.ecommerceweb;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.ecommerce.ecommercedomain.Category;
import pl.ecommerce.ecommercedomain.Product;
import pl.ecommerce.ecommercerepository.CategoryRepository;
import pl.ecommerce.ecommercerepository.ProductRepository;

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

    public void bootstrap1() {
        Category category1 = new Category("Samochody", "Znajdują się tutaj samochody");
        Product product1 = new Product("Audi1", "szybie audi", category1,
                BigDecimal.valueOf(100000), "");
        category1.getProducts().add(product1);

        productRepository.save(product1);
        categoryRepository.save(category1);
    }
}
