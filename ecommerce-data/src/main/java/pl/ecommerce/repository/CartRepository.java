package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.Cart;
import pl.ecommerce.data.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findById(Long id);


    Optional<Cart> findByOwnerId(Long ownerId);
}
