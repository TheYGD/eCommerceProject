package pl.ecommerce.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
public class UserCredentials extends BaseEntity {

    private String email;
    private String username;
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    private User userAccount;
}
