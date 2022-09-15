package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import pl.ecommerce.data.dto.UserInformationDto;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class LoginServiceIntegrationTest {

    @Autowired
    private LoginService loginService;


    @Test
    @Transactional
    void register() {
        // given
        String username = "asd"; // to short for normal user, its checked on mapping to UserInformationDto from request
        String email = "asdasdasd"; // also invalid

        UserInformationDto userInformationDto = UserInformationDto.builder()
                .username(username)
                .email(email)
                .dateOfBirth("2012-12-12")
                .firstName("Name")
                .lastName("Last")
                .password("Passswort")
                .phoneNumber("121212121")
                .build();

        BindingResult bindingResult = Mockito.spy(BindingResult.class);
        when( bindingResult.hasErrors() ).thenReturn( false );

        // when + then - void function, no errors
        loginService.register(userInformationDto, bindingResult);
    }

    @Test
    @Transactional
    void registerUsernameAndEmailTaken() {
        // given
        String username = "asd"; // to short for normal user, its checked on mapping to UserInformationDto from request
        String email = "asdasdasd"; // also invalid

        UserInformationDto userInformationDto = UserInformationDto.builder()
                .username(username)
                .email(email)
                .dateOfBirth("2012-12-12")
                .firstName("Name")
                .lastName("Last")
                .password("Passswort")
                .phoneNumber("121212121")
                .build();

        BindingResult bindingResult = Mockito.spy(BindingResult.class);
        when( bindingResult.hasErrors() ).thenReturn( false );


        // when
        loginService.register(userInformationDto, bindingResult);
        // now it will have errors
        when( bindingResult.hasErrors() ).thenReturn( true );
        loginService.register(userInformationDto, bindingResult);


        // then
        ArgumentCaptor<ObjectError> argumentCaptor = ArgumentCaptor.forClass(ObjectError.class);
        verify( bindingResult, times(2) ).addError( argumentCaptor.capture() );
        assertEquals( "username", argumentCaptor.getAllValues().get(0).getObjectName() );
        assertEquals( "email", argumentCaptor.getAllValues().get(1).getObjectName() );
    }

}