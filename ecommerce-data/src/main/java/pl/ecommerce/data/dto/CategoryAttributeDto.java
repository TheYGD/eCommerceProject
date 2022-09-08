package pl.ecommerce.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.ecommerce.exceptions.ItemNotFoundException;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class CategoryAttributeDto {

    private long id;
    private String category;
    private String name;

    // list has values for enums, it is empty for number
    private List<PseudoEnumValueDto> values;


    public String getValueFromProductAttributes(List<ProductAttributeDto> productAttributes) {
        ProductAttributeDto productAttribute = productAttributes.stream()
                .filter( attr -> attr.getCategoryAttribute().getId() == getId() )
                .findFirst()
                .orElse(null);

        if (productAttribute == null) {
            return "";
        }

        if (values.size() == 0) {
            return productAttribute.getValue().toString();
        }

        return values.stream()
                .filter( enumValue -> enumValue.getNumber() == productAttribute.getValue().longValue() )
                .findFirst()
                .orElseThrow( () -> new ItemNotFoundException("Attribute value not found!") )
                .getNumber()
                .toString();
    }
}
