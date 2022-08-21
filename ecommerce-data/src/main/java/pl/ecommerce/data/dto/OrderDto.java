package pl.ecommerce.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {

    private String addressLine1; // street, company etc.
    private String addressLine2; // apartment, building, floor etc.
    private String city;
    private String state; // voivodeship, state is more universal
    private String postalCode;
    private String country;

    private String paymentOption;
}
