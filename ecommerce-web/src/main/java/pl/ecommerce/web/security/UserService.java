package pl.ecommerce.web.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.ecommerce.exceptions.exceptions.InvalidUserCredentialsException;
import pl.ecommerce.repository.UserCredentialsRepository;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private UserCredentialsRepository userCredentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userCredentialsRepository.findByUsernameOrEmail(usernameOrEmail
                        , usernameOrEmail)
                .orElseThrow( () -> new InvalidUserCredentialsException("Invalid username/email or pasword"));
    }

}
