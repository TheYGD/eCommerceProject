package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.SoldProduct;

@Repository
public interface SoldProductRepository extends JpaRepository<SoldProduct, Long> {

    SoldProduct save(SoldProduct soldProduct);
}
