package pl.ecommerce.data.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PasswordChangeDto {

    @NotNull
    private String oldPassword;

    @NotNull
    @Pattern(regexp = ".{8,50}")
    private String newPassword;
}
