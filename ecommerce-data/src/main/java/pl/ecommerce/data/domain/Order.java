package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<SoldProduct> soldProductsList;

    @OneToOne(cascade = CascadeType.ALL) // todo - for now
    private Address address;

    @OneToOne
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private LocalDateTime dateTime;

    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;

}