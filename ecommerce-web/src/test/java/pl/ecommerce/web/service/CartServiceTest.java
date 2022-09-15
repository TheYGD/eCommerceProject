package pl.ecommerce.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.repository.AvailableProductRepository;
import pl.ecommerce.repository.CartRepository;
import pl.ecommerce.repository.CartToExpireRepository;
import pl.ecommerce.repository.ProductInCartRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private AvailableProductRepository availableProductRepository;
    @Mock
    private ProductInCartRepository productInCartRepository;
    @Mock
    private CartToExpireRepository cartToExpireRepository;

    @InjectMocks
    private CartService cartService;


    @BeforeEach
    void setUp() {

    }


    @Test
    void createCartForUnloggedUser() {
        HttpServletRequest request = Mockito.spy(HttpServletRequest.class);
        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);

        when( request.getCookies() ).thenReturn( null );
        when( cartRepository.save( any() ) ).thenAnswer( method -> method.getArgument(0) );

        Cart cartUnlogged = cartService.getCart( null, request, response );

        assertNotNull(cartUnlogged);
    }


    @Test
    void changeProductsQuantityWithExcess() {
        // given
        final int productQuantity = 5;
        final int productInCartQuantity = 3;
        final int newQuantity = 6;

        AvailableProduct availableProduct = new AvailableProduct();
        availableProduct.setQuantity(productQuantity);
        availableProduct.setProduct(new Product());

        ProductInCart productInCart = new ProductInCart();
        productInCart.setProduct(availableProduct);
        productInCart.setQuantity(productInCartQuantity);

        Cart cart = new Cart();
        cart.setProductList( List.of(productInCart) );

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUserAccount(new User());
        userCredentials.getUserAccount().setId(1L);

        when( availableProductRepository.findById(any()) ).thenReturn(Optional.of(availableProduct));


        // when + then
        assertThrows(InvalidArgumentException.class, () ->
                cartService.changeProductsQuantity(userCredentials, null, newQuantity, null, null));
    }
}