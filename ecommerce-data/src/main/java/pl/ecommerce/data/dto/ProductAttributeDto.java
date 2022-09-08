package pl.ecommerce.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.ecommerce.exceptions.ItemNotFoundException;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeDto {

    private BigDecimal value;
    private CategoryAttributeDto categoryAttribute;


    public String getStringValue() {
        if (categoryAttribute.getValues().size() == 0) {
            return value.toString();
        }

        return categoryAttribute.getValues().stream()
                .filter( enumValue -> enumValue.getNumber() == value.longValue() )
                .findFirst()
                .orElseThrow( () -> new ItemNotFoundException("Attribute value not found!") )
                .getNumber()
                .toString();
    }
}
