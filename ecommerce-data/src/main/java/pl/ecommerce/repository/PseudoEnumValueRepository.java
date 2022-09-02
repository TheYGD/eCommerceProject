package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.PseudoEnumValue;

@Repository
public interface PseudoEnumValueRepository extends JpaRepository<PseudoEnumValue, Long> {


}
