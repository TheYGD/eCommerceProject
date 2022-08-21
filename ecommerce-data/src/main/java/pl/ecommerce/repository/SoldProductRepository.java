<<<<<<< HEAD
package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.SoldProduct;

@Repository
public interface SoldProductRepository extends JpaRepository<SoldProduct, Long> {

    SoldProduct save(SoldProduct soldProduct);
=======
package pl.ecommerce.repository;public class SoldProductRepository {
>>>>>>> d78251f8f37aee427c19d07ddd89cfeb0e56cd04
}
