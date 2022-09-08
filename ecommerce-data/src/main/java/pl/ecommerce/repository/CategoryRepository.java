package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.CategoryAttribute;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Deprecated
    Optional<Category> findById(Long id);

    Optional<Category> findByOrderId(Long id);

    Category save(Category category);

    boolean existsByName(String name);


    List<Category> findAllByOrderIdBetween(long adjustStartId, long adjustEndId);


    List<Category> findAllByCategoryAttributesContaining(CategoryAttribute categoryAttribute);
}
