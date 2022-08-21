<<<<<<< HEAD
package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithQuantity extends BaseEntity {

    @ManyToOne
    private Product product;

    private Integer quantity;
=======
package pl.ecommerce.data.domain;public class ProductWithQuantity {
>>>>>>> d78251f8f37aee427c19d07ddd89cfeb0e56cd04
}
