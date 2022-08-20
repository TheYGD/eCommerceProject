package pl.ecommerce.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @OneToOne
    @JoinColumn(name="cart_id")
    private Cart cart;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="residential_adress_id")
    private Address residentialAddress;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="user_shipping_addresses",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="shipping_adress_id"))
    private List<Address> shippingAddressList;
    private String imageId;

}