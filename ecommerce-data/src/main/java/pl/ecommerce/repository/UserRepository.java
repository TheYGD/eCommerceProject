package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);


    boolean existsByImageId(String id);
}
