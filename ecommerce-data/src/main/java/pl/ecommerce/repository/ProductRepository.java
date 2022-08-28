package pl.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);


    Product save(Product product);


    boolean existsByImage(String image);


    Page<Product> findAll(Pageable pageable);  // to be deleted?


    Page<Product> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String query, String query1,
                                                                                     Pageable pageable);

    Page<Product> findAllBySeller(User seller, Pageable pageable);


    Page<Product> findAllByCategory(Category category, Pageable pageable);


    @Query("SELECT p FROM products p WHERE p.category = :category AND ( p.name LIKE %:query% OR p.description LIKE %:query% )")
    Page<Product> findAllByCategoryAndQuery(@Param("category") Category category, @Param("query") String query,
                                            Pageable pageable);
}
