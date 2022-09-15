package pl.ecommerce.web.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.AvailableProductRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class ProductServiceTest {

    @Mock
    private MessageService messageService;

    @Mock
    private AvailableProductRepository availableProductRepository;

    @InjectMocks
    private ProductService productService;

    private static UserCredentials userCredentials;
    private static Map<String, String> formData;


    @BeforeAll
    static void setUp() {
        userCredentials = new UserCredentials();
        formData = new HashMap<>();
        formData.put("titleId", "5");
        formData.put("content", "content");
    }


    @Test
    void askAboutProductMissingFormData() {
        // given
        Throwable exception = assertThrows(InvalidArgumentException.class, () ->
                productService.askAboutProduct(userCredentials, 2L, new HashMap<>() ));

        // when + then
        assertEquals( "Error! Try again later.", exception.getMessage() );
    }

    @Test
    public void askAboutProductCauseNotFound() {
        // given
        Map<String, String> values = new HashMap<>();
        values.put("titleId", "512");
        values.put("content", "content");

        // when
        Throwable exception = assertThrows( ItemNotFoundException.class, () ->
                productService.askAboutProduct(userCredentials, 2L, values));

        // then
        assertEquals( "Message cause not found!", exception.getMessage() );
    }

    @Test
    void askAboutProductWrongCause() {
        // given
        Map<String, String> values = new HashMap<>();
        values.put("titleId", "1");
        values.put("content", "content");

        // when
        Throwable exception = assertThrows( InvalidArgumentException.class, () ->
                productService.askAboutProduct(userCredentials, 2L, values));

        // then
        assertEquals( "Error! Try again later.", exception.getMessage() );
    }


    @Test
    void askAboutProduct() {
        // given
        AvailableProduct availableProduct = new AvailableProduct();
        availableProduct.setProduct(new Product());
        availableProduct.getProduct().setName("product");
        availableProduct.getProduct().setSeller(new User());
        when( availableProductRepository.findById(any()) ).thenReturn(Optional.of(availableProduct));
        when( messageService.createChat(any(), any(), any(), any(), any(), any())).thenReturn( 1L );

        // when
        Long chatId = productService.askAboutProduct(userCredentials, 1L, formData);

        // then
        assertEquals( 1L, chatId );
    }
}
