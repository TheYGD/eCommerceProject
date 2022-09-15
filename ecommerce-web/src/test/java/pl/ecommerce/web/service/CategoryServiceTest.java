package pl.ecommerce.web.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.CategoryAttribute;
import pl.ecommerce.data.domain.PseudoEnum;
import pl.ecommerce.data.dto.CategoryAttributeDto;
import pl.ecommerce.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private static List<Category> categoryList;


    @BeforeAll
    static void setUp() {
        categoryList = new ArrayList<>();

        Category cat1 = new Category(1L, "category1", "", new LinkedList<>(), null,
                new LinkedList<>() );
        CategoryAttribute attr1 = new CategoryAttribute(cat1, "attr1", true, null);
        attr1.setId(1L);
        CategoryAttribute attr2 = new CategoryAttribute(cat1, "attr2", false, new PseudoEnum());
        attr2.setId(2L);
        cat1.getCategoryAttributes().addAll( List.of(attr1, attr2) );

        Category cat2 = new Category(2L, "category2", "", new LinkedList<>(), cat1,
                new LinkedList<>() );
        cat1.getChildren().add(cat2);
        CategoryAttribute attr3 = new CategoryAttribute(cat2, "attr3", true, null);
        attr3.setId(3L);
        cat2.getCategoryAttributes().add( attr3 );

        categoryList.addAll( List.of(cat1, cat2) );
    }


    @Test
    void findAll() {
        Category cat1 = categoryList.get(0);
        when(categoryRepository.findByOrderId(1L)).thenReturn( Optional.of(cat1) );

        List<Category> categories = categoryService.findAll();
        assertEquals( 2, categories.size() );
        verify( categoryRepository ).findByOrderId(1L);
    }

    @Test
    void getCategoryAttributeDtos() {
        Category cat2 = categoryList.get(1);
        when(categoryRepository.findByOrderId(2L)).thenReturn( Optional.of(cat2) );

        List<CategoryAttributeDto> categoryAttributes = categoryService.getCategoryAttributeDtos(2L);
        assertEquals( 3, categoryAttributes.size());
    }

    @Test
    void getCategoryOrderEnd() {
        Category cat1 = categoryList.get(0);
        Category cat2 = categoryList.get(1);

        long cat1End = categoryService.getCategoryOrderEnd( cat1 );
        long cat2End = categoryService.getCategoryOrderEnd( cat2 );

        assertEquals( cat1End, cat2End );
    }
}