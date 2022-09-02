package pl.ecommerce.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.ecommerce.data.domain.AvailableProduct;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Long parentId;


    public CategoryDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
