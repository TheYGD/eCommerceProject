package pl.ecommerce.data.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
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
