package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.dto.UserInformationDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Test
    void getUserInformation() {
        // given
        User user = new User();
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setDateOfBirth(LocalDate.now());
        user.setPhoneNumber("123123123");

        UserCredentials credentials = new UserCredentials();
        credentials.setUserAccount(user);
        credentials.setUsername("login");
        credentials.setEmail("email@email.email");

        // when
        UserInformationDto userInformationDto = profileService.getUserInformation(credentials);

        // then
        assertEquals( credentials.getUsername(), userInformationDto.getUsername() );
        assertEquals( credentials.getEmail(), userInformationDto.getEmail() );
        assertEquals( user.getFirstName(), userInformationDto.getFirstName() );
        assertEquals( user.getLastName(), userInformationDto.getLastName() );
        assertEquals( user.getDateOfBirthString(), userInformationDto.getDateOfBirth() );
        assertEquals( user.getPhoneNumber(), userInformationDto.getPhoneNumber() );
    }
}