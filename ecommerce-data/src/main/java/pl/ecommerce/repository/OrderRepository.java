package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Order;
import pl.ecommerce.data.domain.User;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order save(Order order);

    List<Order> findAllByBuyerOrderByDateTimeDescIdDesc(User userAccount);

    List<Order> findAllBySellerOrderByDateTimeDescIdDesc(User userAccount);
}
