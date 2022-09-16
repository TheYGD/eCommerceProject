package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ManageProductService manageProductService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;


    @Test
    @Transactional
    void postOrder() {
        // given
        OrderDto orderDto = new OrderDto();
        orderDto.setAddressLine1("first line");
        orderDto.setAddressLine2("second one");
        orderDto.setCity("City");
        orderDto.setCountry("Country");
        orderDto.setPostalCode("XX-YYY");
        orderDto.setPaymentOption( PaymentMethod.BANK_TRANSFER.name() );

        User user = createUser("user", "email");
        User seller = createUser("seller", "sellersemail");
        AvailableProduct availableProduct = createProduct("name", "desc", "12.00", 20
                , seller.getCredentials());
        cartService.addProduct(user.getCart(), availableProduct, 10);

        // when
        orderService.postOrder( user.getCredentials(), orderDto );
        List<Order> orders = profileService.getOrders(user.getCredentials());
        AvailableProduct productFromOrder = orders.get(0).getSoldProductsList().get(0).getProduct()
                .getAvailableProduct();

        // then
        assertEquals( 1, orders.size() );
        assertEquals( availableProduct.getId(), productFromOrder.getId() );
    }

    @Test
    @Transactional
    void orderBelongsToUser() {
        // given
        OrderDto orderDto = new OrderDto();
        orderDto.setAddressLine1("first line");
        orderDto.setAddressLine2("second one");
        orderDto.setCity("City");
        orderDto.setCountry("Country");
        orderDto.setPostalCode("XX-YYY");
        orderDto.setPaymentOption( PaymentMethod.BANK_TRANSFER.name() );

        User user = createUser("user", "email");
        User seller = createUser("seller", "sellersemail");
        AvailableProduct availableProduct = createProduct("name", "desc", "12.00", 20
                , seller.getCredentials());
        cartService.addProduct(user.getCart(), availableProduct, 10);

        // when
        orderService.postOrder( user.getCredentials(), orderDto );
        Order placedOrder = profileService.getOrders(user.getCredentials()).get(0);
        orderService.orderBelongsToUser( placedOrder, user.getCredentials() ); // no exception
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