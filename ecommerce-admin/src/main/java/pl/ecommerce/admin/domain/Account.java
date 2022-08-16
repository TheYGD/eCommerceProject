package pl.ecommerce.admin.domain;

import lombok.Getter;
import lombok.Setter;
import pl.ecommerce.data.entity.BaseEntity;

import javax.management.relation.Role;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public class Account extends BaseEntity {

    private String username;
    private String password;

    private String firstName;
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    private Role role;
}
