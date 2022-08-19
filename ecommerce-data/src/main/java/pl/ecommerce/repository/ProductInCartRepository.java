package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.entity.ProductInCart;

@Repository
public interface ProductInCartRepository extends JpaRepository<ProductInCart, Long> {

    ProductInCart save(ProductInCart productInCart);
}
