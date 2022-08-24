package pl.ecommerce.web.service;

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
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProfileService {

    @Value("${pl.ecommerce.records_on_page}")
    private int RECORDS_ON_PAGE;
    private final SoldProductsGroupRepository soldProductsGroupRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductSort productSort;

    public ProfileService(SoldProductsGroupRepository soldProductsGroupRepository, OrderRepository orderRepository,
                          ProductRepository productRepository, UserRepository userRepository,
                          UserCredentialsRepository userCredentialsRepository, PasswordEncoder passwordEncoder,
                          ProductSort productSort) {
        this.soldProductsGroupRepository = soldProductsGroupRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.productSort = productSort;
    }



    public List<SoldProductsGroup> getSoldProductsGroupList(UserCredentials userCredentials) {
        return soldProductsGroupRepository.findAllBySeller(userCredentials.getUserAccount());
    }

    public List<Order> getOrderedProducts(UserCredentials userCredentials) {
        return orderRepository.findAllByBuyer(userCredentials.getUserAccount());
    }

    public Page<Product> getOwnProducts(UserCredentials userCredentials, int pageNr, int sortOption) {
        Pageable pageable = PageRequest.of(pageNr - 1, RECORDS_ON_PAGE, productSort.getSort(sortOption));
        return productRepository.findAllBySeller(userCredentials.getUserAccount(), pageable);
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
    public String updateUserInformation(UserCredentials userCredentials, UserInformationDto userInformationDto,
                                        BindingResult bindingResult) {
        if (bindingResult.getErrorCount() != 1 || !bindingResult.hasFieldErrors("password")) {
            return "Data not valid";
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

        return "Information updated.";
    }

    public String changePassword(UserCredentials userCredentials, PasswordChangeDto passwordChangeDto,
                                 BindingResult bindingResult) {
        if (passwordChangeDto.getOldPassword() == null) {
            return "Error!";
        }

        if (!passwordEncoder.matches( passwordChangeDto.getOldPassword(), userCredentials.getPassword() ) ) {
            return "Incorrect password.";
        }

        if (bindingResult.hasErrors()) {
            return "New password format is incorrect!";
        }

        if (passwordChangeDto.getNewPassword().equals(passwordChangeDto.getOldPassword())) {
            return "New password is the same as the old one!";
        }

        userCredentials.setPassword( passwordEncoder.encode(passwordChangeDto.getNewPassword()) );
        userCredentialsRepository.save(userCredentials);

        return "Password changed successfully.";
    }
}
