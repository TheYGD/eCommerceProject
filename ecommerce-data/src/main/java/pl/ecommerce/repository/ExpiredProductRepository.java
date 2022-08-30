package pl.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.ExpiredProduct;
import pl.ecommerce.data.domain.User;


@Repository
public interface ExpiredProductRepository extends JpaRepository<ExpiredProduct, Long> {

    ExpiredProduct save(AvailableProduct ExpiredProduct);


    @Query( "SELECT ep FROM ExpiredProduct ep " +
            "INNER JOIN ep.product p " +
            "WHERE p.seller = :seller")
    Page<ExpiredProduct> findAllBySeller(@Param("seller") User seller, Pageable pageable);
}
