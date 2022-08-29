package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "available_products")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableProduct extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private int BoughtQuantity;

    private boolean promoted;



    public String getStructuredDescription() {
        return product.getStructuredDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailableProduct availableProduct = (AvailableProduct) o;
        return getProduct() == availableProduct.getProduct();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
