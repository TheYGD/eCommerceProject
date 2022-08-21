<<<<<<< HEAD
package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoldProduct extends BaseEntity {

    @OneToOne
    private ProductWithQuantity productWithQuantity;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;



    public Product getProduct() {
        return productWithQuantity.getProduct();
    }

    public Integer getQuantity() {
        return productWithQuantity.getQuantity();
    }
=======
package pl.ecommerce.data.domain;public class SoldProduct {
>>>>>>> d78251f8f37aee427c19d07ddd89cfeb0e56cd04
}
