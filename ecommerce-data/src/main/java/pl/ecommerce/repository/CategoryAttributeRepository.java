package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.CategoryAttribute;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {


}
