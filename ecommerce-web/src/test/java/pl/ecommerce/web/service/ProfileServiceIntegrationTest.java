package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.data.dto.PasswordChangeDto;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.data.dto.UserInformationDto;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class ProfileServiceIntegrationTest {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private ProductService productService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ManageProductService manageProductService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;



    @Test
    @Transactional
    void getSoldProductsList() {
        // given
        User buyer = createUser("buyer", "asdmkasd@asd.pl");
        User seller = createUser("seller", "xasd@xaasd.pl");
        Category category = createCategory("my-cat", null);
        AvailableProduct product = createProduct("namee", "122.25", category.getOrderId(), seller);

        cartService.addProduct(buyer.getCart(), product,2);
        postOrder(buyer);

        // when
        List<Order> soldProducts = profileService.getSoldProductsList(seller.getCredentials());
        Product soldProduct = soldProducts.get(0).getSoldProductsList().get(0).getProduct();

        // then
        assertEquals( 1, soldProducts.size() );
        assertEquals( product.getProduct().getId(), soldProduct.getId() );
    }

    @Test
    @Transactional
    void getOrders() {
        // given
        User buyer = createUser("buyer", "asdmkasd@asd.pl");
        User seller = createUser("seller", "xasd@xaasd.pl");
        Category category = createCategory("my-cat", null);
        AvailableProduct product = createProduct("namee", "122.25", category.getOrderId(), seller);

        cartService.addProduct(buyer.getCart(), product,2);
        postOrder(buyer);

        // when
        List<Order> orders = profileService.getOrders(buyer.getCredentials());
        Product soldProduct = orders.get(0).getSoldProductsList().get(0).getProduct();

        // then
        assertEquals( 1, orders.size() );
        assertEquals( product.getProduct().getId(), soldProduct.getId() );
    }

    @Test
    @Transactional
    void getOwnProducts() {
        // given
        User seller = createUser("seller", "xasd@xaasd.pl");
        Category category = createCategory("my-cat", null);
        AvailableProduct product1 = createProduct("namee", "122.25", category.getOrderId(), seller);
        AvailableProduct product2 = createProduct("namee2", "22.25", category.getOrderId(), seller);
        AvailableProduct product3 = createProduct("namee3", "43.00", category.getOrderId(), seller);

        // when
        Page<AvailableProduct> ownProducts = profileService.getOwnProducts( seller.getCredentials(), 1,
                0 );

        // then
        assertEquals( 3, ownProducts.getTotalElements() );
        assertThat( ownProducts.getContent() ).map( x -> x.getId() ).contains( product1.getId(), product2.getId(),
                product3.getId() );
    }

    @Test
    @Transactional
    void getUserInformation() {
        // given
        UserInformationDto userInformationDto = UserInformationDto.builder()
                .username("userLogin")
                .email("someEMail@oas.pl")
                .password("password")
                .phoneNumber("123456789")
                .firstName("imie")
                .lastName("etwas")
                .dateOfBirth("2007-12-12")
                .build();

        Long id = loginService.saveUser(userInformationDto);
        User user = userRepository.findById(id)
                .orElseThrow();

        // when
        UserInformationDto userFromProfile = profileService.getUserInformation( user.getCredentials() );

        // then
        assertEquals( userInformationDto.getFirstName(), userFromProfile.getFirstName() );
        assertEquals( userInformationDto.getLastName(), userFromProfile.getLastName() );
        assertEquals( userInformationDto.getUsername(), userFromProfile.getUsername() );
        assertEquals( userInformationDto.getEmail(), userFromProfile.getEmail() );
        assertEquals( userInformationDto.getPhoneNumber(), userFromProfile.getPhoneNumber() );
        assertEquals( userInformationDto.getDateOfBirth(), userFromProfile.getDateOfBirth() );
    }

    @Test
    @Transactional
    void updateUserInformation() {
        // given
        UserInformationDto userInformationDto = UserInformationDto.builder()
                .username("userLogin")
                .email("someEMail@oas.pl")
                .password("password")
                .phoneNumber("123456789")
                .firstName("imie")
                .lastName("etwas")
                .dateOfBirth("2007-12-12")
                .build();

        Long id = loginService.saveUser(userInformationDto);
        User user = userRepository.findById(id)
                .orElseThrow();
        userInformationDto.setUsername("new_username");

        BindingResult bindingResult = Mockito.spy( BindingResult.class );
        when( bindingResult.getErrorCount() ).thenReturn( 1 );
        when( bindingResult.hasFieldErrors("password") ).thenReturn( true );

        // when
        profileService.updateUserInformation( user.getCredentials(), userInformationDto, bindingResult );
        UserInformationDto userFromProfile = profileService.getUserInformation( user.getCredentials() );


        // then
        assertEquals( userInformationDto.getFirstName(), userFromProfile.getFirstName() );
        assertEquals( userInformationDto.getLastName(), userFromProfile.getLastName() );
        assertEquals( userInformationDto.getUsername(), userFromProfile.getUsername() );
        assertEquals( userInformationDto.getEmail(), userFromProfile.getEmail() );
        assertEquals( userInformationDto.getPhoneNumber(), userFromProfile.getPhoneNumber() );
        assertEquals( userInformationDto.getDateOfBirth(), userFromProfile.getDateOfBirth() );
    }

    @Test
    @Transactional
    void changePassword() {
        // given
        String oldPassword = "passport";
        String newPassword = "password";
        UserInformationDto userInformationDto = UserInformationDto.builder()
                .username("userLogin")
                .email("someEMail@oas.pl")
                .password(oldPassword)
                .phoneNumber("123456789")
                .firstName("imie")
                .lastName("etwas")
                .dateOfBirth("2007-12-12")
                .build();

        Long id = loginService.saveUser(userInformationDto);
        User user = userRepository.findById(id)
                .orElseThrow();

        BindingResult bindingResult = Mockito.spy( BindingResult.class );
        when( bindingResult.hasErrors() ).thenReturn( false );

        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setOldPassword(oldPassword);
        passwordChangeDto.setNewPassword(newPassword);


        // when + then, as we just want not to get an exception
        profileService.changePassword( user.getCredentials(), passwordChangeDto, bindingResult );
    }


    private Category createCategory(String name, Category catParent) {
        Category parent = catParent != null ? catParent : categoryRepository.findByOrderId(1L).orElse(null);
        long orderId = categoryRepository.count() + 1;

        Category category = Category.builder()
                .parent(parent)
                .name(name)
                .description("desc")
                .orderId(orderId)
                .children(new LinkedList<>())
                .categoryAttributes(new LinkedList<>())
                .build();

        category = categoryRepository.save(category);

        if (parent != null) {
            parent.getChildren().add(category);
            categoryRepository.save(parent);
        }

        return category;
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

    private AvailableProduct createProduct(String name, String price, long categoryId, User seller) {
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .price(price)
                .description("description")
                .quantity(10)
                .image( new MockMultipartFile( "img", new byte[]{} ) )
                .category(categoryId)
                .build();


        Long productId = manageProductService.createProduct( seller.getCredentials(), productDto, new HashMap<>());
        return productRepository.findById(productId)
                .orElseThrow( () -> new RuntimeException("PRODUCT NOT FOUND") )
                .getAvailableProduct();
    }

    private void postOrder(User buyer) {
        OrderDto orderDto = new OrderDto();
        orderDto.setAddressLine1("first line");
        orderDto.setAddressLine2("second one");
        orderDto.setCity("City");
        orderDto.setCountry("Country");
        orderDto.setPostalCode("XX-YYY");
        orderDto.setPaymentOption( PaymentMethod.BANK_TRANSFER.name() );

        orderService.postOrder(buyer.getCredentials(), orderDto);
    }
}