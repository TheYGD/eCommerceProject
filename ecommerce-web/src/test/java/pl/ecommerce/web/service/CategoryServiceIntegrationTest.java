package pl.ecommerce.web.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.CategoryAttribute;
import pl.ecommerce.data.domain.PseudoEnum;
import pl.ecommerce.data.domain.PseudoEnumValue;
import pl.ecommerce.data.dto.CategoryAttributeDto;
import pl.ecommerce.data.dto.PseudoEnumValueDto;
import pl.ecommerce.repository.CategoryAttributeRepository;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.PseudoEnumRepository;
import pl.ecommerce.repository.PseudoEnumValueRepository;

import javax.transaction.Transactional;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryAttributeRepository categoryAttributeRepository;
    @Autowired
    private PseudoEnumRepository pseudoEnumRepository;



    @Test
    @Transactional
    void findAll() {
        Category category1 = getCategory(null);
        Category category2 = getCategory(category1);

        List<Category> categories = categoryService.findAll();

        assertTrue( categories.containsAll( List.of(category1, category2) ) );
    }

    @Test
    @Transactional
    void getCategoryAttributeDtos() {
        // given
        Category category = getCategory(null);

        CategoryAttribute attr1 = CategoryAttribute.builder()
                .category(category)
                .name("attr1")
                .number(true)
                .build();

        PseudoEnum pseudoEnum = new PseudoEnum();
        PseudoEnumValue val1 = new PseudoEnumValue( 1L,  pseudoEnum, "val1" );
        PseudoEnumValue val2 = new PseudoEnumValue( 2L,  pseudoEnum, "val2" );
        pseudoEnum.setValues( new LinkedList<>( List.of(val1, val2) ) );

        pseudoEnum = pseudoEnumRepository.save(pseudoEnum);

        CategoryAttribute attr2 = CategoryAttribute.builder()
                .category(category)
                .name("attr2")
                .number(false)
                .pseudoEnum(pseudoEnum)
                .build();

        attr1 = categoryAttributeRepository.save(attr1);
        attr2 = categoryAttributeRepository.save(attr2);

        category.setCategoryAttributes( new LinkedList<>( List.of(attr1, attr2) ) );
        categoryRepository.save(category);


        // when
        List<CategoryAttributeDto> attributeDtos = categoryService.getCategoryAttributeDtos(category.getOrderId());


        // then
        for (int i = 0; i < category.getAllCategoryAttributes().size(); i++) {
            CategoryAttribute attr = category.getAllCategoryAttributes().get(i);
            CategoryAttributeDto dto = attributeDtos.get(i);

            assertEquals( attr.getId(), dto.getId() );
            assertEquals( attr.getName(), dto.getName() );
            assertEquals( attr.getCategory().getName(), dto.getCategory() );

            if (!attr.isNumber()) {
                for (int j = 0; j < attr.getPseudoEnum().getValues().size(); j++) {
                    PseudoEnumValue value = attr.getPseudoEnum().getValues().get(j);
                    PseudoEnumValueDto valDto = dto.getValues().get(j);

                    assertEquals( value.getNumber(), valDto.getNumber() );
                    assertEquals( value.getName(), valDto.getName() );
                }
            }
        }
    }

    @Test
    @Transactional
    void getCategoryOrderEnd() {
        // given
        Category cat1 = getCategory(null);
            Category cat2 = getCategory(cat1);
                Category cat3 = getCategory(cat2);
                    Category cat4 = getCategory(cat3);
                    Category cat5 = getCategory(cat3);
                    Category cat6 = getCategory(cat3);
            Category cat7 = getCategory(cat1);

        // when
        long end1 = categoryService.getCategoryOrderEnd(cat1);
        long end2 = categoryService.getCategoryOrderEnd(cat2);
        long end3 = categoryService.getCategoryOrderEnd(cat3);
        long end4 = categoryService.getCategoryOrderEnd(cat4);
        long end5 = categoryService.getCategoryOrderEnd(cat5);
        long end6 = categoryService.getCategoryOrderEnd(cat6);
        long end7 = categoryService.getCategoryOrderEnd(cat7);

        // then
        assertEquals(end1, end7);

        assertEquals(end2, end1 - 1);
        assertEquals(end2, end3);
        assertEquals(end2, end4 + 2);
        assertEquals(end2, end5 + 1);
        assertEquals(end2, end6);

        assertEquals(cat6.getOrderId(), end6);
        assertEquals(cat7.getOrderId(), end7);
    }

    private Category getCategory(Category catParent) {
        Category parent = catParent != null ? catParent : categoryRepository.findByOrderId(1L).orElse(null);
        long orderId = categoryRepository.count() + 1;

        Category category = Category.builder()
                .parent(parent)
                .name("cat")
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
}