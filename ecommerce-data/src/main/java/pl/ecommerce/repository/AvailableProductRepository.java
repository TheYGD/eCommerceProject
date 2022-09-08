package pl.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.User;

import java.util.Optional;

@Repository
public interface AvailableProductRepository extends JpaRepository<AvailableProduct, Long>,
        JpaSpecificationExecutor<AvailableProduct> {

    Optional<AvailableProduct> findById(Long id);


    AvailableProduct save(AvailableProduct availableProduct);


    @Query("SELECT ap FROM AvailableProduct ap INNER JOIN ap.product p WHERE p.image = :image")
    Optional<AvailableProduct> findByImage(@Param("image") String image);


    Page<AvailableProduct> findAll(Pageable pageable);  // to be deleted?


    @Deprecated
    @Query("SELECT ap FROM AvailableProduct ap INNER JOIN ap.product p WHERE p.seller = :seller")
    Page<AvailableProduct> findAllBySeller(@Param("seller") User seller, Pageable pageable);

}
