package pl.ecommerce.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Collection;

@Entity
@Getter
@Setter
public class UserCredentials extends BaseEntity implements UserDetails {

    private String email;
    private String username;
    private String password;

    private boolean locked;

    @OneToOne(cascade = CascadeType.ALL)
    private User userAccount;


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
