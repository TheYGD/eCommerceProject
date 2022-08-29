package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.AvailableProduct;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);

    Product save(Product product);

    boolean existsByImage(String image);

    Optional<Product> findByAvailableProduct(AvailableProduct availableProduct);
}
