package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInCart extends BaseEntity {

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private ProductWithQuantity productWithQuantity;


    public Product getProduct() {
        return productWithQuantity.getProduct();
    }

    public Integer getQuantity() {
        return productWithQuantity.getQuantity();
    }
}