package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.ProductInCart;

@Repository
public interface ProductInCartRepository extends JpaRepository<ProductInCart, Long> {

    ProductInCart save(ProductInCart productInCart);

    void deleteAllByProduct(AvailableProduct availableProduct);
}
