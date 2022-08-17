package pl.ecommerce.data.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserRegisterDto {

    @NotNull
    @Email
    @Pattern(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    @Pattern(regexp = "[\\w+]{6,20}")
    private String username;

    @NotNull
    @Pattern(regexp = "[\\w+]{8,50}")
    private String password;


    @NotNull
    @Pattern(regexp = "[\\w+]{2,30}")
    private String firstName;

    @NotNull
    @Pattern(regexp = "[\\w+]{2,30}")
    private String lastName;

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private String dateOfBirth;

    @Pattern(regexp = "((00|\\+) ?\\d{1,3})?(\\d{9}|\\d{3} \\d{3} \\d{3})") // not sure if it works, test will appear soon
    // todo this should be extended
    private String phoneNumber;
}
