package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);

    Product save(Product product);

    boolean existsByImageId(String id);


    List<Product> findAll();


    List<Product> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String query, String query1);
}
