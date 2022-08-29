package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "sold_products")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoldProduct extends BaseEntity {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Product product;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


    public SoldProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
