package pl.ecommerce.admin.domain;

import lombok.Getter;
import lombok.Setter;
import pl.ecommerce.domain.entity.BaseEntity;

import javax.management.relation.Role;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Account extends BaseEntity {

    private String username;
    private String password;

    private String firstName;
    private String lastName;

    private Role role;
}
