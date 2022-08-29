package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "carts_to_expire")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartToExpire extends BaseEntity {

    @OneToOne
    private Cart cart;

    private LocalDate expirationDate;
}
