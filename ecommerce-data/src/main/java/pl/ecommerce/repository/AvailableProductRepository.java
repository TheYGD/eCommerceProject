package pl.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.User;

import java.util.Optional;

@Repository
public interface AvailableProductRepository extends JpaRepository<AvailableProduct, Long> {

    Optional<AvailableProduct> findById(Long id);


    AvailableProduct save(AvailableProduct availableProduct);


    @Query("SELECT ap FROM AvailableProduct ap INNER JOIN ap.product p WHERE p.image = :image")
    Optional<AvailableProduct> findByImage(@Param("image") String image);


    Page<AvailableProduct> findAll(Pageable pageable);  // to be deleted?


    @Query("SELECT ap FROM AvailableProduct ap INNER JOIN ap.product p WHERE lower(p.name) LIKE lower(concat('%', :query, '%')) OR p.description LIKE lower(concat('%', :query, '%'))")
    Page<AvailableProduct> findAllByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT ap FROM AvailableProduct ap INNER JOIN ap.product p WHERE p.seller = :seller")
    Page<AvailableProduct> findAllBySeller(@Param("seller") User seller, Pageable pageable);


    @Query("SELECT ap FROM AvailableProduct ap INNER JOIN ap.product p WHERE p.category.id BETWEEN :categoryOrderStart AND :categoryOrderEnd")
    Page<AvailableProduct> findAllByCategory(@Param("categoryOrderStart") long orderStart, @Param("categoryOrderEnd") long orderEnd, Pageable pageable);


    @Query("SELECT ap FROM AvailableProduct ap INNER JOIN ap.product p WHERE p.category.id BETWEEN :categoryOrderStart AND :categoryOrderEnd AND ( lower(p.name) LIKE lower(concat('%', :query, '%')) OR p.description LIKE lower(concat('%', :query, '%')) )")
    Page<AvailableProduct> findAllByCategoryAndQuery(@Param("categoryOrderStart") long orderStart, @Param("categoryOrderEnd") long orderEnd, @Param("query") String query,
                                                     Pageable pageable);
}
