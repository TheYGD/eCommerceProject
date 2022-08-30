package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "products_in_carts")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInCart extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private AvailableProduct product;

    private int quantity;



    public Product getProduct() {
        return product.getProduct();
    }

    public AvailableProduct getAvailableProduct() {
        return product;
    }
}
