package pl.ecommerce.data.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity(name = "carts_to_expire")
@Table(name = "carts_to_expire")
@NoArgsConstructor
@AllArgsConstructor
public class CartToExpire extends BaseEntity {

    @OneToOne
    private Cart cart;
    private LocalDate expirationDate;
}
