<<<<<<< HEAD
package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.ProductWithQuantity;

@Repository
public interface ProductWithQuantityRepository extends JpaRepository<ProductWithQuantity, Long> {

    ProductWithQuantity save(ProductWithQuantity productWithQuantity);
=======
package pl.ecommerce.repository;public interface ProductWithQuantityRepository {
>>>>>>> d78251f8f37aee427c19d07ddd89cfeb0e56cd04
}
