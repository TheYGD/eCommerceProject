package pl.ecommerce.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="orders")
public class Order extends BaseEntity {

    @OneToMany(mappedBy = "order")
    private List<SoldProductsGroup> productsGroupList;

    @OneToOne
    private Address address;

    @OneToOne
    private User buyer;

    private LocalDateTime dateTime;

    private PaymentOption paymentOption;

}