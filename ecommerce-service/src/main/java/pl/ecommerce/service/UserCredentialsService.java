package pl.ecommerce.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.domain.entity.UserCredentials;
import pl.ecommerce.exceptions.exceptions.InvalidUserCredentialsException;
import pl.ecommerce.repository.UserCredentialsRepository;

@Service
@AllArgsConstructor
public class UserCredentialsService {

    private UserCredentialsRepository userCredentialsRepository;

    public UserCredentials findByUsernameOrEmail(String usernameOrEmail) {
        return userCredentialsRepository.findByUsernameOrEmail(usernameOrEmail
                , usernameOrEmail)
                .orElseThrow( () -> new InvalidUserCredentialsException("Invalid username/email or pasword"));
    }

}
