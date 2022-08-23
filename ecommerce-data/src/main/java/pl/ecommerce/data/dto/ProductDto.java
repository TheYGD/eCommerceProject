package pl.ecommerce.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {



    @NotBlank
    @Size(min=5, max=50)
    private String name;

    @NotBlank
    @Size(min=20, max=20000)
    private String description;

    @NotBlank
    private String category;

    @Min(1) @Max(1000000)
    private int quantity;

    @Pattern(regexp = "\\d+((\\.|,)\\d{1,2})?")
    private String price;

    @NotNull
    private MultipartFile image;
}
