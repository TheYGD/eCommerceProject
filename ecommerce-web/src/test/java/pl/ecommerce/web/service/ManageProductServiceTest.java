package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.exceptions.ForbiddenException;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
class ManageProductServiceTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ManageProductService manageProductService;


    @Test
    void getOwnProduct() {
        // given
        User seller = new User();
        seller.setCredentials(new UserCredentials());
        seller.getCredentials().setUserAccount(seller);

        AvailableProduct availableProduct = new AvailableProduct();
        availableProduct.setProduct(new Product());
        availableProduct.getProduct().setSeller(seller);

        when( productService.getProduct(any()) ).thenReturn( availableProduct );


        // when
        AvailableProduct product = manageProductService.getProduct(seller.getCredentials(), any());

        // then
        assertNotNull(product);
    }

    @Test
    void getNotYourProduct() {
        // given
        User seller = new User();
        seller.setCredentials(new UserCredentials());
        AvailableProduct availableProduct = new AvailableProduct();
        availableProduct.setProduct(new Product());
        availableProduct.getProduct().setSeller(seller);

        when( productService.getProduct(any()) ).thenReturn( availableProduct );

        // when + then
        assertThrows(ForbiddenException.class, () ->
                manageProductService.getProduct(new UserCredentials(), any()));
    }

    @Test
    void getProductDto() {
        // given
        Category category = new Category();
        category.setName("category");

        Product product = Product.builder()
                .availableProduct(new AvailableProduct())
                .price(new BigDecimal("123"))
                .description("description")
                .category(new Category())
                .build();
        product.getCategory().setId(1L);
        product.getAvailableProduct().setQuantity(23);
        product.getAvailableProduct().setProduct(product);

        List attributes = new LinkedList<>();
        CategoryAttribute catAttr1 = new CategoryAttribute(category, "attr1", true, null);
        catAttr1.setId(1L);
        ProductAttribute attr1 = new ProductAttribute(catAttr1, product, new BigDecimal("123"));

        CategoryAttribute catAttr2 = new CategoryAttribute(category, "attr2", true, null);
        catAttr2.setId(2L);
        ProductAttribute attr2 = new ProductAttribute(catAttr2, product, new BigDecimal("15"));

        CategoryAttribute catAttr3 = new CategoryAttribute(category, "attr3", true, null);
        catAttr3.setId(1L);
        ProductAttribute attr3 = new ProductAttribute(catAttr3, product, new BigDecimal("7"));

        attributes.addAll( List.of(attr1, attr2, attr3) );

        product.setAttributes(attributes);


        // when
        ProductDto productDto = manageProductService.getProductDto( product.getAvailableProduct() );


        // then
        assertEquals( product.getName(), productDto.getName() );
        assertEquals( product.getDescription(), productDto.getDescription() );
        assertEquals( product.getCategory().getOrderId(), productDto.getCategory() );
        assertEquals( product.getAvailableProduct().getQuantity(), productDto.getQuantity() );
        assertEquals( product.getPrice().toString(), productDto.getPrice() );

        for (int i = 0; i < product.getAttributes().size(); i++) {
            assertEquals( product.getAttributes().get(i).getCategoryAttribute().getId(),
                    productDto.getAttributes().get(i).getCategoryAttribute().getId() );
        }

    }
}