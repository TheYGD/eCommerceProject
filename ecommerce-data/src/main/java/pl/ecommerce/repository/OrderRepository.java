<<<<<<< HEAD
package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order save(Order order);
=======
package pl.ecommerce.repository;public interface OrderRepository {
>>>>>>> d78251f8f37aee427c19d07ddd89cfeb0e56cd04
}
