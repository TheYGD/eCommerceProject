package pl.ecommerce.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = {"pl.ecommerce.web", "pl.ecommerce.data", "pl.ecommerce.admin"})
@EntityScan(basePackages = {"pl.ecommerce.data"})
@EnableJpaRepositories(basePackages = {"pl.ecommerce.repository"})
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
