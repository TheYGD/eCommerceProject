package pl.ecommerce.ecommerceweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = {"pl.ecommerce.ecommerceweb", "pl.ecommerce.ecommerceservice",
        "pl.ecommerce.ecommerceexceptions"})
@EntityScan(basePackages = {"pl.ecommerce.ecommercedomain"})
@EnableJpaRepositories(basePackages = {"pl.ecommerce.ecommercerepository"})
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
