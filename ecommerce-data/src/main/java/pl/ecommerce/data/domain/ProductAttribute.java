package pl.ecommerce.data.domain;

import lombok.*;
import pl.ecommerce.exceptions.ItemNotFoundException;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_attributes")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttribute extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_attribute_id")
    private CategoryAttribute categoryAttribute;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal value; // either actual value or the enum_value_id


    public String getStringValue() {
        if (categoryAttribute.isNumber()) {
            return value.toString();
        }

        return categoryAttribute.getPseudoEnum().getValues().stream()
                .filter( enumValue -> enumValue.getNumber() == value.longValue() )
                .findFirst()
                .orElseThrow( () -> new ItemNotFoundException("Attribute value not found!") )
                .getName();
    }
}
