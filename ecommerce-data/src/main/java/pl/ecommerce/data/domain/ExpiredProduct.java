package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "expired_products")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpiredProduct extends GivenIdEntity {

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private int soldQuantity;
}
