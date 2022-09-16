package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.repository.AvailableProductRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;
    @Autowired
    private ManageProductService manageProductService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private AvailableProductRepository availableProductRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;


    @Test
    @Transactional
    void changeProductsQuantity() {
        // given
        User seller = createUser("user1", "email1");
        User buyer = createUser("user2", "email2");
        AvailableProduct availableProduct = createProduct( "product1", "with desc", "12.00",
                10, seller.getCredentials() );
        Cart cart = cartService.getCartLogged( buyer.getCredentials() );
        int fixedQuantityBefore = 2;
        cartService.addProductToCart( buyer.getCredentials(), availableProduct.getId(), fixedQuantityBefore, null, null );
        int quantityBeforeChange = cart.getProductList().get(0).getQuantity();

        // when
        int fixedQuantityAfter = 3;
        cartService.changeProductsQuantity( buyer.getCredentials(), availableProduct.getId(), fixedQuantityAfter,
                null, null );
        cart = cartService.getCartLogged( buyer.getCredentials() );
        int quantityAfterChange = cart.getProductList().get(0).getQuantity();

        // then
        assertThat(cart.getProductList().size()).isEqualTo(1);
        assertEquals( fixedQuantityBefore, quantityBeforeChange );
        assertEquals( fixedQuantityAfter, quantityAfterChange );
    }

    @Test
    @Transactional
    void removeProduct() {
        // given
        User seller = createUser("user1", "email1");
        User buyer = createUser("user2", "email2");
        AvailableProduct availableProduct = createProduct( "product1", "with desc", "12.00",
                10, seller.getCredentials() );
        Cart cart = cartService.getCartLogged( buyer.getCredentials() );
        cartService.addProductToCart( buyer.getCredentials(), availableProduct.getId(), 2, null, null );

        // when
        cartService.removeProduct(buyer.getCredentials(), availableProduct.getId(), null, null);
        cart = cartService.getCartLogged( buyer.getCredentials() );

        // then
        assertThat(cart.getProductList().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void addProductToCart() {
        // given
        User seller = createUser("user1", "email1");
        User buyer = createUser("user2", "email2");
        AvailableProduct availableProduct = createProduct( "product1", "with desc", "12.00",
                10, seller.getCredentials() );
        Cart cart = cartService.getCartLogged( buyer.getCredentials() );

        // when
        cartService.addProductToCart( buyer.getCredentials(), availableProduct.getId(), 2, null, null );
        ProductInCart productInCart = cart.getProductList().get(0);

        // then
        assertThat(cart.getProductList().size()).isEqualTo(1);
        assertThat(productInCart).isNotNull();

        assertEquals( availableProduct.getId(), productInCart.getAvailableProduct().getId() );
    }

    @Test
    @Transactional
    void addProductWith0Quantity() {
        // given
        User seller = createUser("user1", "email1");
        User buyer = createUser("user2", "email2");
        AvailableProduct availableProduct = createProduct( "product1", "with desc", "12.00",
                10, seller.getCredentials() );
        Cart cart = cartService.getCartLogged( buyer.getCredentials() );

        // when + then
        Throwable exception = assertThrows( InvalidArgumentException.class, () ->
                cartService.addProduct(cart, availableProduct, 0) );
        assertEquals( "Product quantity must be greater than 0!", exception.getMessage() );
    }

    @Test
    @Transactional
    void addYourOwnProduct() {
        // given
        User seller = createUser("user1", "email1");
        AvailableProduct availableProduct = createProduct( "product1", "with desc", "12.00",
                10, seller.getCredentials() );
        Cart cart = cartService.getCartLogged( seller.getCredentials() );

        // when + then
        Throwable exception = assertThrows( InvalidArgumentException.class, () ->
                cartService.addProduct(cart, availableProduct, 2) );
        assertEquals( "Can't add your own products to cart!", exception.getMessage() );
    }

    @Test
    @Transactional
    void getCartLoggedButWithNullCredentials() {
         Throwable exception = assertThrows( RuntimeException.class, () ->
                cartService.getCartLogged(null) );

         assertEquals("UserCredentials cannot be null!", exception.getMessage());
    }

    @Test
    @Transactional
    void mergeCartsAfterLogin() {
        // given
        Cart cart = getUnloggedCart();
        User loggedInUser = createUser("user", "email");

        User seller = createUser("seller", "sellersemail");
        AvailableProduct availableProduct = createProduct( "product1", "with desc", "12.00",
                10, seller.getCredentials() );
        cartService.addProduct( cart, availableProduct, 2);

        // when
        cartService.mergeCartsAfterLogin( loggedInUser.getCredentials(), cart.getId() );
        Cart  mergedCart = cartService.getCartLogged(loggedInUser.getCredentials());

        // then
        assertEquals( 1, mergedCart.getProductList().size() );
        assertEquals( availableProduct.getId(), mergedCart.getProductList().get(0).getAvailableProduct().getId() );
    }



    private Cart getUnloggedCart() {
        HttpServletRequest request = Mockito.spy(HttpServletRequest.class);
        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        when( request.getCookies() ).thenReturn(null);

        Cart cart = cartService.getCartWithoutReload(null, request, response);

        return cart;
    }

    private AvailableProduct createProduct(String name, String description, String price, int quantity, UserCredentials credentials) {
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .price(price)
                .description(description)
                .quantity(quantity)
                .image( new MockMultipartFile( "img", new byte[]{} ) )
                .category(1)
                .build();

        Long productId = manageProductService.createProduct( credentials, productDto, new HashMap<>());

        return productRepository.findById(productId)
                .orElseThrow( () -> new RuntimeException("PRODUCT NOT FOUND") )
                .getAvailableProduct();
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