package pl.ecommerce.admin.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.ecommerce.data.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Getter
@Setter
public class Account extends BaseEntity implements UserDetails, Serializable {

    private String username;
    private String password;

    private String firstName;
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private boolean locked;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return !locked;
    }
}
