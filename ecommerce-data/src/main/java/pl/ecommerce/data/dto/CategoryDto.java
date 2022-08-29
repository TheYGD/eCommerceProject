package pl.ecommerce.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.ecommerce.data.domain.AvailableProduct;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    private String name;
    private String description;
    private List<AvailableProduct> availableProducts;


    public CategoryDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
