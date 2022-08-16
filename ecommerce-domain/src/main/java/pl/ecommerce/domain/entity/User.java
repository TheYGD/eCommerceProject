package pl.ecommerce.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class User extends BaseEntity {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    private UserCredentials credentials;

    @OneToOne(cascade = CascadeType.ALL)
    private Address residentialAddress;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Address> shippingAddressList;
    private String imageUrl;


}
