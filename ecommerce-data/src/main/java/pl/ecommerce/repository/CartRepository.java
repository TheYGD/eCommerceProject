package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Cart;
import pl.ecommerce.data.domain.User;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    boolean existsById(String s);

    Optional<Cart> findById(Long id);


    Optional<Cart> findByOwnerId(Long ownerId);

}
