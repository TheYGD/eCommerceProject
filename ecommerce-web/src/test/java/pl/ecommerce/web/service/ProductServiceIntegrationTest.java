package pl.ecommerce.web.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ManageProductService manageProductService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    void findById() {
        // given
        User seller = createUser("sellerd");
        Category category = createCategory("our_category", null);
        AvailableProduct product = createProduct("product", "22.50", category.getOrderId(), seller);

        // when
        AvailableProduct foundProduct = productService.findById(product.getId()).getAvailableProduct();

        // then
        assertEquals( product.getId(), foundProduct.getId() );
        assertEquals( product.getProduct().getName(), foundProduct.getProduct().getName() );
    }

    @Test
    @Transactional
    void findProductsBySeller() {
        // given
        User seller1 = createUser("seller#1");
        User seller2 = createUser("seller#2");
        Category category = createCategory("our_category", null);
        Category all = categoryRepository.findByOrderId(1L).orElseThrow();

        AvailableProduct product1 = createProduct("product", "880", category.getOrderId(), seller1);
        AvailableProduct product2 = createProduct("product", "10", category.getOrderId(), seller2);
        AvailableProduct product3 = createProduct("product", "760", category.getOrderId(), seller2);

        // when
        Page<AvailableProduct> foundProducts = productService.findProducts(all, "", 1, 0,
                null, new HashMap<>(), seller2.getId());

        // then
        assertEquals( 2, foundProducts.getTotalElements() );
        assertThat( foundProducts ).map( GivenIdEntity::getId ).contains(product2.getId(), product3.getId());
    }

    @Test
    @Transactional
    void findProductsByQuery() {
        // given
        User seller1 = createUser("seller#1");
        User seller2 = createUser("seller#2");
        Category category = createCategory("our_category", null);
        Category all = categoryRepository.findByOrderId(1L).orElseThrow();
        String name = "productt";
        AvailableProduct product1 = createProduct(name, "20", category.getOrderId(), seller1);
        AvailableProduct product2 = createProduct(name, "50", category.getOrderId(), seller2);
        AvailableProduct product3 = createProduct(name, "260", category.getOrderId(), seller2);

        // when
        Page<AvailableProduct> foundProducts = productService.findProducts(all, name, 1, 0,
                null, new HashMap<>(), null);

        // then
        assertEquals(3, foundProducts.getTotalElements());
        assertThat(foundProducts).map(GivenIdEntity::getId).contains(product1.getId(), product2.getId(),
                product3.getId());
    }

    @Test
    @Transactional
    void findProductsByCategory() {
        // given
        User seller1 = createUser("seller#1");
        User seller2 = createUser("seller#2");
        Category category = createCategory("our_category", null);
        Category all = categoryRepository.findByOrderId(1L).orElseThrow();

        AvailableProduct product1 = createProduct("productt", "20", category.getOrderId(), seller1);
        AvailableProduct product2 = createProduct("productt", "21", category.getOrderId(), seller2);
        AvailableProduct product3 = createProduct("productt", "50", category.getOrderId(), seller2);
        AvailableProduct product4 = createProduct("productt", "120", all.getOrderId(), seller2);

        // when
        Page<AvailableProduct> foundProducts = productService.findProducts(category, "", 1, 0,
                null, new HashMap<>(), null);

        // then
        assertEquals(3, foundProducts.getTotalElements());
        assertThat(foundProducts).map(GivenIdEntity::getId).contains(product1.getId(), product2.getId(),
                product3.getId());
    }

    @Test
    @Transactional
    void findProductsByCategoryAndPrice() {
        // given
        User seller1 = createUser("seller#1");
        User seller2 = createUser("seller#2");
        Category category = createCategory("our_category", null);
        Category all = categoryRepository.findByOrderId(1L).orElseThrow();
        String price1 = "20";
        String price2 = "40";
        String priceParam = price1 + "x" + price1;

        AvailableProduct product1 = createProduct("productt", price1, category.getOrderId(), seller1);
        AvailableProduct product2 = createProduct("productt", price1, category.getOrderId(), seller2);
        AvailableProduct product3 = createProduct("productt", price2, category.getOrderId(), seller2);
        AvailableProduct product4 = createProduct("productt", price1, all.getOrderId(), seller2);

        // when
        Page<AvailableProduct> foundProducts = productService.findProducts(category, "", 1, 0,
                priceParam, new HashMap<>(), null);

        // then
        assertEquals(2, foundProducts.getTotalElements());
        assertThat(foundProducts).map(GivenIdEntity::getId).contains(product1.getId(), product2.getId());
    }

    @Test
    @Transactional
    void getProduct() {
        // given
        User seller = createUser("seller");
        Category category = createCategory("our_category", null);
        AvailableProduct product = createProduct("name", "29.99", category.getOrderId(), seller);

        // when
        AvailableProduct foundProduct = productService.getProduct(product.getId());

        // then
        assertEquals( product.getProduct().getName(), foundProduct.getProduct().getName() );
    }

    @Test
    @Transactional
    void getCategory() {
        // given
        Category category = createCategory("our_category", null);

        // when
        Category foundCategory = productService.getCategory(category.getOrderId());

        // then
        assertEquals( category.getName(), foundCategory.getName());
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

    private User createUser(String username) {
        UserCredentials credentials = new UserCredentials();
        credentials.setUserAccount(new User());
        credentials.setUsername(username);
        User user = credentials.getUserAccount();
        user.setCredentials(credentials);
        return userRepository.save(user);
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

}