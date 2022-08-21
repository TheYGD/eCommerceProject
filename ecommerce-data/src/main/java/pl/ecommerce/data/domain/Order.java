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