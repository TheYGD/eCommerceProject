package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.domain.entity.UserCredentials;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    Optional<UserCredentials> findByUsernameOrEmail(String email, String username);

    Optional<UserCredentials> findByEmail(String email);

    Optional<UserCredentials> findByUsername(String username);

    UserCredentials save(UserCredentials userCredentials);
}
