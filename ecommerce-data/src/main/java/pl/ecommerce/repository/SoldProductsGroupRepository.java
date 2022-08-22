package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.SoldProductsGroup;
import pl.ecommerce.data.domain.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface SoldProductsGroupRepository extends JpaRepository<SoldProductsGroup, Long> {

    SoldProductsGroup save(SoldProductsGroup soldProductsGroup);


    List<SoldProductsGroup> findAllBySeller(User userAccount);
}
