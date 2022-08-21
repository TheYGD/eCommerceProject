<<<<<<< HEAD
package pl.ecommerce.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Order extends BaseEntity {

    @OneToMany(mappedBy="order")
    private List<SoldProduct> productList;

    @OneToOne
    private Address address;

    @OneToOne
    private User buyer;

    private LocalDateTime dateTime;

    private PaymentOption paymentOption;

}
=======
package pl.ecommerce.data.domain;public class Order {
}
>>>>>>> d78251f8f37aee427c19d07ddd89cfeb0e56cd04
