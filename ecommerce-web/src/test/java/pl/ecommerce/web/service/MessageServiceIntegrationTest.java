package pl.ecommerce.web.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.Cart;
import pl.ecommerce.data.domain.MessageCause;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.dto.MessageDto;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.data.mapper.UserMapper;
import pl.ecommerce.exceptions.ForbiddenException;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.repository.UserRepository;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class MessageServiceIntegrationTest {

    @Autowired
    private MessageService messageService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;


    @BeforeEach
    @Transactional
    void setUp() {
        UserInformationDto userInformationDto1 = UserInformationDto.builder()
                .username("user1")
                .email("email1")
                .dateOfBirth("2012-12-12")
                .firstName("Name")
                .lastName("Last")
                .password("Passswort")
                .phoneNumber("121212121")
                .build();
        user1 = UserMapper.INSTANCE.dtoToEntity(userInformationDto1);
        UserCredentials userCredentials1 = user1.getCredentials();
        userCredentials1.setUserAccount(user1);

        UserInformationDto userInformationDto2 = UserInformationDto.builder()
                .username("user2")
                .email("email2")
                .dateOfBirth("2012-12-12")
                .firstName("Name")
                .lastName("Last")
                .password("Passswort")
                .phoneNumber("121212121")
                .build();
        user2 = UserMapper.INSTANCE.dtoToEntity(userInformationDto2);
        UserCredentials userCredentials2 = user2.getCredentials();
        userCredentials2.setUserAccount(user2);

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
    }


    @Test
    @Transactional
    void findChats() {
        // given
        int chatSize = messageService.findChats( user1.getCredentials() ).size();

        // when
        messageService.createChat(user1, user2, MessageCause.ORDER_INCOMPLETE, "", "", "");
        int chatSizeAfterNewChat = messageService.findChats( user1.getCredentials() ).size();

        // then
        assertEquals( chatSize + 1, chatSizeAfterNewChat );
    }

    @Test
    @Transactional
    void findMessagesFromChat() {
        // given
        String messageContent = "nice 1st message :)";
        long id = messageService.createChat(user1, user2, MessageCause.ORDER_INCOMPLETE, messageContent, "", "");

        // when
        Page<MessageDto> messagePage = messageService.findMessagesFromChat( user1.getCredentials(), id, 0 );
        MessageDto message = messagePage.getContent().get(0);

        // then
        assertEquals( 1, messagePage.getTotalElements() );
        assertEquals( user1.getCredentials().getUsername(), message.getAuthor() );
        assertEquals( messageContent, message.getContent() );
    }

    @Test
    @Transactional
    void sendMessage() {
        // given
        String messageContent3 = "\"my response\"";
        String messageContent4 = "\"me 3rd :) message\"";
        long id = messageService.createChat(user1, user2, MessageCause.ORDER_INCOMPLETE, "new chat", "", "");

        // when
        messageService.sendMessage(user1.getCredentials(), id, "second message");
        messageService.sendMessage(user2.getCredentials(), id, messageContent3);
        messageService.sendMessage(user1.getCredentials(), id, messageContent4);
        Page<MessageDto> messagePage = messageService.findMessagesFromChat( user1.getCredentials(), id, 0 );

        MessageDto message3 = messagePage.getContent().get(2);
        MessageDto message4 = messagePage.getContent().get(3);

        // then
        assertEquals( 4, messagePage.getTotalElements() );
        assertEquals( user2.getCredentials().getUsername(), message3.getAuthor() );
        assertEquals( messageContent3.substring(1, messageContent3.length() - 1), message3.getContent() );
        assertEquals( user1.getCredentials().getUsername(), message4.getAuthor() );
        assertEquals( messageContent4.substring(1, messageContent4.length() - 1), message4.getContent() );
    }

    @Test
    @Transactional
    void createChatWithThemself() {
        assertThrows( InvalidArgumentException.class, () ->
                messageService.createChat(user1, user1, MessageCause.ORDER_INCOMPLETE, "", "", "") );
    }

    @Test
    @Transactional
    void closeChat() {
        // given
        String messageContent = "init chat";
        long id = messageService.createChat(user1, user2, MessageCause.ORDER_INCOMPLETE, messageContent, "", "");

        // when
        messageService.closeChat(user1.getCredentials(), id);
        Page<MessageDto> messagePage = messageService.findMessagesFromChat( user1.getCredentials(), id, 0 );

        // then
        Throwable exception1 = assertThrows( ForbiddenException.class, () ->
                messageService.sendMessage(user1.getCredentials(), id, "let me try"));
        Throwable exception2 = assertThrows( ForbiddenException.class, () ->
                messageService.sendMessage(user2.getCredentials(), id, "now its my turn"));

        assertEquals( "This chat is closed!", exception1.getMessage() );
        assertEquals( "This chat is closed!", exception2.getMessage() );
        assertEquals( 1, messagePage.getTotalElements() );
    }

    @Test
    @Transactional
    void reopenChat() {
        // given
        String messageContent = "\"after reopen\"";
        long id = messageService.createChat(user1, user2, MessageCause.ORDER_INCOMPLETE, "init chat", "", "");

        // when1
        messageService.closeChat(user1.getCredentials(), id);

        Throwable exception1 = assertThrows( ForbiddenException.class, () ->
                messageService.sendMessage(user1.getCredentials(), id, "let me try"));
        Throwable exception2 = assertThrows( ForbiddenException.class, () ->
                messageService.sendMessage(user2.getCredentials(), id, "now its my turn"));

        // when2
        messageService.reopenChat(user1.getCredentials(), id);
        messageService.sendMessage(user1.getCredentials(), id, messageContent);
        Page<MessageDto> messagePage = messageService.findMessagesFromChat( user1.getCredentials(), id, 0 );
        MessageDto message = messagePage.getContent().get(1);


        // then
        assertEquals( "This chat is closed!", exception1.getMessage() );
        assertEquals( "This chat is closed!", exception2.getMessage() );

        assertEquals( 2, messagePage.getTotalElements() );
        assertEquals( messageContent.substring(1, messageContent.length() - 1), message.getContent() );
    }
}