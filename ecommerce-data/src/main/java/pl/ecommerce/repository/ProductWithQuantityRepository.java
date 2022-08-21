package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.ProductWithQuantity;

@Repository
public interface ProductWithQuantityRepository extends JpaRepository<ProductWithQuantity, Long> {

    ProductWithQuantity save(ProductWithQuantity productWithQuantity);
}
