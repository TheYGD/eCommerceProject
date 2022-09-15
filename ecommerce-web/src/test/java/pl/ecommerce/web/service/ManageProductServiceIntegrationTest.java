package pl.ecommerce.web.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.dto.ProductAttributeDto;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;
import pl.ecommerce.specification.params.ProductAttributeParam;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class ManageProductServiceIntegrationTest {

    @Autowired
    private ManageProductService manageProductService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private Product product;
    private ProductDto productDto;


    @BeforeEach
    @Transactional
    void setUp() {
        productDto = ProductDto.builder()
                .name("Name")
                .price("55.00")
                .description("description")
                .quantity(10)
                .image( new MockMultipartFile( "img", new byte[]{} ) )
                .category(1)
                .build();


        UserCredentials credentials = new UserCredentials();
        credentials.setUserAccount(new User());
        credentials.setUsername("name");
        User user = credentials.getUserAccount();
        user.setCredentials(credentials);
        userRepository.save(user);

        Long productId = manageProductService.createProduct( credentials, productDto, new HashMap<>());
        product = productRepository.findById(productId)
                .orElseThrow( () -> new RuntimeException("PRODUCT NOT FOUND") );
    }


    @Test
    @Transactional
    void createProduct() {
        assertEquals( productDto.getName(), product.getName() );
        assertEquals( productDto.getDescription(), product.getDescription() );
        assertEquals( productDto.getPrice(), product.getPrice().toString() );
        assertEquals( productDto.getQuantity(), product.getAvailableProduct().getQuantity() );
        assertEquals( productDto.getCategory(), product.getCategory().getOrderId() );
    }

    @Test
    @Transactional
    void editProduct() {
        // given
        productDto.setName("new_name123");
        productDto.setPrice("12.00");

        // when
        manageProductService.editProduct( product.getSeller().getCredentials(), productDto, product.getId(), new HashMap<>() );
        product = productRepository.findById(product.getId())
                .orElseThrow( () -> new RuntimeException("PRODUCT NOT FOUND") );

        // then
        assertEquals( productDto.getName(), product.getName() );
        assertEquals( productDto.getDescription(), product.getDescription() );
        assertEquals( productDto.getPrice(), product.getPrice().toString() );
        assertEquals( productDto.getQuantity(), product.getAvailableProduct().getQuantity() );
        assertEquals( productDto.getCategory(), product.getCategory().getOrderId() );
    }

    @Test
    @Transactional
    void deleteProduct() {
        // when
        manageProductService.deleteProduct( product.getSeller().getCredentials(), product.getId());

        // then
        boolean deleted = productRepository.findById(product.getId())
                .orElseThrow( () -> new RuntimeException("PRODUCT NOT FOUND") )
                .getAvailableProduct() == null;
        assertTrue(deleted);
    }

    @Test
    @Transactional
    void getProductDto() {
        // when
        ProductDto dto = manageProductService.getProductDto( product.getAvailableProduct() );

        // then
        assertEquals( product.getName(), dto.getName() );
        assertEquals( product.getDescription(), dto.getDescription() );
        assertEquals( product.getPrice().toString(), dto.getPrice() );
        assertEquals( product.getAvailableProduct().getQuantity(), dto.getQuantity() );
        assertEquals( product.getCategory().getOrderId(), dto.getCategory() );
    }
}