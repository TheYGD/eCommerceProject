package pl.ecommerce.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.ecommerce.data.domain.Product;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    private String name;
    private String description;
    private List<Product> products;


    public CategoryDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
