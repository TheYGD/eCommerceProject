package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.repository.UserRepository;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class UserCredentialsServiceIntegrationTest {

    @Autowired
    private UserCredentialsService userCredentialsService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    public void findByUsernameOrEmail() {
        // given
        String username = "usernamee";
        String email = "email@email.com";
        User user = createUser(username, email);

        // when
        UserCredentials userCredentials1 = userCredentialsService.findByUsernameOrEmail(username);
        UserCredentials userCredentials2 = userCredentialsService.findByUsernameOrEmail(email);

        // then
        assertThat( userCredentials1 ).isNotNull();
        assertEquals( userCredentials1.getId(), userCredentials2.getId() );
    }

    private User createUser(String username, String email) {
        UserInformationDto userInformationDto = UserInformationDto.builder()
                .username(username)
                .email(email)
                .password("password")
                .build();

        Long id = loginService.saveUser(userInformationDto);
        return userRepository.findById(id)
                .orElseThrow();
    }
}