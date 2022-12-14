package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Cart;
import pl.ecommerce.data.domain.CartToExpire;

import java.time.LocalDate;

@Repository
public interface CartToExpireRepository extends JpaRepository<CartToExpire, Long> {

    CartToExpire save(CartToExpire entity);

    void deleteAllByExpirationDateBefore(LocalDate date);

    void deleteByCart(Cart otherCart);
}
