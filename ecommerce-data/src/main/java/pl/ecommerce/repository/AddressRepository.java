<<<<<<< HEAD
package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address save(Address address);
=======
package pl.ecommerce.repository;public interface AddressRepository {
>>>>>>> d78251f8f37aee427c19d07ddd89cfeb0e56cd04
}
