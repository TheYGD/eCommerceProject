package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order save(Order order);
}
