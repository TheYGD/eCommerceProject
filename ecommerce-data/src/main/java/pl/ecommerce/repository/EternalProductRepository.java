package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.EternalProduct;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EternalProductRepository extends JpaRepository<EternalProduct, Long> {

    Optional<EternalProduct> findById(Long id);

    EternalProduct save(EternalProduct product);

    boolean existsByImage(String image);

    Optional<EternalProduct> findByProduct(Product product);
}
