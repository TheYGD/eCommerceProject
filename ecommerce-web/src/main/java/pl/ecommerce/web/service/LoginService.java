package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.data.domain.Cart;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.mapper.UserMapper;
import pl.ecommerce.repository.CartRepository;
import pl.ecommerce.repository.UserCredentialsRepository;
import pl.ecommerce.repository.UserRepository;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class LoginService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;


    public void register(UserInformationDto userInformationDto, BindingResult bindingResult) {

        if (isUsernameTaken(userInformationDto.getUsername())) {
            bindingResult.addError(
                    new ObjectError("username", "This username already exists."));
        }

        if (isEmailTaken(userInformationDto.getEmail())) {
            bindingResult.addError(
                    new ObjectError("email", "This email is already taken."));
        }

        if (!bindingResult.hasErrors()) {
            saveUser(userInformationDto);
        }
    }

    private boolean isEmailTaken(String email) {
        return userCredentialsRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameTaken(String username) {
        return userCredentialsRepository.findByUsername(username).isPresent();
    }

    @Transactional
    public Long saveUser(UserInformationDto userInformationDto) {
        userInformationDto.setPassword( passwordEncoder.encode(userInformationDto.getPassword()) );
        User user = UserMapper.INSTANCE.dtoToEntity(userInformationDto);

        UserCredentials userCredentials = user.getCredentials();
        userCredentials.setUserAccount(user);

        Cart cart = cartRepository.save(new Cart());
        cart.setOwner(user);
        user.setCart(cart);

        userCredentialsRepository.save(userCredentials);
        user = userRepository.save(user);

        return user.getId();
    }
}
