package pl.ecommerce.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "addresses")

@Getter
@Setter
public class Address extends BaseEntity {

    private String addressLine1; // street, company etc.
    private String addressLine2; // apartment, building, floor etc.
    private String city;
    private String state; // voivodeship, state is more universal
    private String postalCode;
    private String country;

}
