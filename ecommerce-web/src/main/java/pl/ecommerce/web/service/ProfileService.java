package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.PasswordChangeDto;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.data.other.ProductSort;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class ProfileService {

    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;



    public List<Order> getSoldProductsList(UserCredentials userCredentials) {
        return orderRepository.findAllBySellerOrderByDateTimeDescIdDesc(userCredentials.getUserAccount());
    }

    public List<Order> getOrders(UserCredentials userCredentials) {
        return orderRepository.findAllByBuyerOrderByDateTimeDescIdDesc(userCredentials.getUserAccount());
    }

    public Page<AvailableProduct> getOwnProducts(UserCredentials userCredentials, int pageNr, int sortOption) {
        Category all = productService.getCategory(1L);
        return productService.findProducts( all, "", pageNr, sortOption, null, new HashMap<>(),
                userCredentials.getUserAccount().getId());
    }

    public UserInformationDto getUserInformation(UserCredentials userCredentials) {
        UserInformationDto userInformation = UserInformationDto.builder()
                .username(userCredentials.getUsername())
                .email(userCredentials.getEmail())
                .firstName(userCredentials.getUserAccount().getFirstName())
                .lastName(userCredentials.getUserAccount().getLastName())
                .dateOfBirth(userCredentials.getUserAccount().getDateOfBirthString())
                .phoneNumber(userCredentials.getUserAccount().getPhoneNumber())
                .build();

        return userInformation;
    }

    @Transactional
    public void updateUserInformation(UserCredentials userCredentials, UserInformationDto userInformationDto,
                                        BindingResult bindingResult) {
        if (bindingResult.getErrorCount() != 1 || !bindingResult.hasFieldErrors("password")) { // we dont look at the password
            throw new InvalidArgumentException("Data not valid");
        }

        User user = userCredentials.getUserAccount();

        userCredentials.setUsername(userInformationDto.getUsername());
        userCredentials.setEmail(userInformationDto.getEmail());
        user.setFirstName(userInformationDto.getFirstName());
        user.setLastName(userInformationDto.getLastName());
        user.setDateOfBirth( LocalDate.parse( userInformationDto.getDateOfBirth() ) );
        user.setPhoneNumber(userInformationDto.getPhoneNumber());

        userRepository.save(user);
        userCredentialsRepository.save(userCredentials);
    }

    public void changePassword(UserCredentials userCredentials, PasswordChangeDto passwordChangeDto,
                                 BindingResult bindingResult) {
        if (passwordChangeDto.getOldPassword() == null) {
            throw new InvalidArgumentException("Error! Try again later.");
        }

        if (!passwordEncoder.matches( passwordChangeDto.getOldPassword(), userCredentials.getPassword() ) ) {
            throw new InvalidArgumentException("Incorrect password.");
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidArgumentException("New password format is incorrect!");
        }

        if (passwordChangeDto.getNewPassword().equals(passwordChangeDto.getOldPassword())) {
            throw new InvalidArgumentException("New password can't be the same as the old one!");
        }

        userCredentials.setPassword( passwordEncoder.encode(passwordChangeDto.getNewPassword()) );
        userCredentialsRepository.save(userCredentials);
    }
}
