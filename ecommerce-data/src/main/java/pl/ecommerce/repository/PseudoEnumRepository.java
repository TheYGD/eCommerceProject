package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.PseudoEnum;

@Repository
public interface PseudoEnumRepository extends JpaRepository<PseudoEnum, Long> {


}
