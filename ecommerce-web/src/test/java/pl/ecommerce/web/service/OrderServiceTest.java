package pl.ecommerce.web.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.Order;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.exceptions.ForbiddenException;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.OrderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
class OrderServiceTest {

    @Mock
    private MessageService messageService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private static UserCredentials userCredentials;
    private static Map<String, String> formData;


    @BeforeAll
    static void setUp() {
        userCredentials = new UserCredentials();
        formData = new HashMap<>();
        formData.put("titleId", "1");
        formData.put("content", "content");
    }



    @Test
    void askAboutOrderMissingFormData() {
        // given
        Throwable exception = assertThrows(InvalidArgumentException.class, () ->
                orderService.askAboutOrder(userCredentials, 2L, new HashMap<>() ));

        // when + then
        assertEquals( "Error! Try again later.", exception.getMessage() );
    }

    @Test
    void askAboutOrderCauseNotFound() {
        // given
        Map<String, String> values = new HashMap<>();
        values.put("titleId", "512");
        values.put("content", "content");

        // when
        Throwable exception = assertThrows( ItemNotFoundException.class, () ->
                orderService.askAboutOrder(userCredentials, 2L, values));

        // then
        assertEquals( "Message cause not found!", exception.getMessage() );
    }

    @Test
    void askAboutOrderWrongCause() {
        // given
        Map<String, String> values = new HashMap<>();
        values.put("titleId", "5");
        values.put("content", "content");

        // when
        Throwable exception = assertThrows( InvalidArgumentException.class, () ->
                orderService.askAboutOrder(userCredentials, 2L, values));

        // then
        assertEquals( "Error! Try again later.", exception.getMessage() );
    }

    @Test
    void askAboutOrderNotUsersOrder() {
        // given
        User buyer = new User();
        buyer.setCredentials(userCredentials);

        Order order = new Order();
        order.setBuyer(buyer);
        order.setId(1L);

        UserCredentials someCredentials = new UserCredentials();
        someCredentials.setUserAccount(new User());
        someCredentials.getUserAccount().setId(1L);

        when( orderRepository.findById(any()) ).thenReturn(Optional.of(order));

        // when + then
        assertThrows( ForbiddenException.class, () ->
                orderService.askAboutOrder(someCredentials, 1L, formData));
    }

    @Test
    void askAboutOrder() {
        // given
        User buyer = new User();
        buyer.setCredentials(userCredentials);
        buyer.getCredentials().setUserAccount(buyer);

        Order order = new Order();
        order.setBuyer(buyer);

        when( orderRepository.findById(any()) ).thenReturn(Optional.of(order));
        when( messageService.createChat(any(), any(), any(), any(), any(), any())).thenReturn( 1L );

        // when
        Long chatId = orderService.askAboutOrder(buyer.getCredentials(), 1L, formData);

        // then
        assertEquals( 1L, chatId );
    }
}