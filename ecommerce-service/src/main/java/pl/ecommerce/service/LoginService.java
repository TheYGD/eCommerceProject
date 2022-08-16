package pl.ecommerce.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import pl.ecommerce.domain.dto.UserRegisterDto;
import pl.ecommerce.domain.entity.User;
import pl.ecommerce.domain.entity.UserCredentials;
import pl.ecommerce.domain.mapper.UserMapper;
import pl.ecommerce.repository.UserCredentialsRepository;
import pl.ecommerce.repository.UserRepository;

@Service
@AllArgsConstructor
public class LoginService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void register(UserRegisterDto userRegisterDto, BindingResult bindingResult) {

        if (isUsernameTaken(userRegisterDto.getUsername())) {
            bindingResult.addError(
                    new ObjectError("username", "This username already exists."));
        }

        if (isEmailTaken(userRegisterDto.getEmail())) {
            bindingResult.addError(
                    new ObjectError("email", "This email is already taken."));
        }

        if (!bindingResult.hasErrors()) {
            saveUser(userRegisterDto);
        }
    }

    private boolean isEmailTaken(String email) {
        return userCredentialsRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameTaken(String username) {
        return userCredentialsRepository.findByUsername(username).isPresent();
    }

    private void saveUser(UserRegisterDto userRegisterDto) {
        userRegisterDto.setPassword( passwordEncoder.encode(userRegisterDto.getPassword()) );
        User user = UserMapper.INSTANCE.userRegisterDtoToUser( userRegisterDto );

        UserCredentials userCredentials = user.getCredentials();
        userCredentials.setUserAccount(user);

        userCredentialsRepository.save(userCredentials);
        userRepository.save(user);
    }
}
