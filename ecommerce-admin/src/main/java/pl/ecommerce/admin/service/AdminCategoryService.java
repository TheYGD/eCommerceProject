package pl.ecommerce.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.CategoryDto;
import pl.ecommerce.exceptions.ItemAlreadyExistsException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.CategoryAttributeRepository;
import pl.ecommerce.repository.PseudoEnumRepository;
import pl.ecommerce.repository.PseudoEnumValueRepository;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final PseudoEnumRepository pseudoEnumRepository;
    private final PseudoEnumValueRepository pseudoEnumValueRepository;

    @Transactional
    public void addCategory(CategoryDto categoryDTO) {

        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new ItemAlreadyExistsException("Category already exists!");
        }

        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription()).build();

        Category categoryParent = categoryRepository.findByOrderId(categoryDTO.getParentId())
                .orElseThrow( () -> new ItemNotFoundException("No such parent category!") );

        adjustIds(categoryParent, category);
    }

    public List<Category> findAll() {
        Category root = categoryRepository.findByOrderId(1L)
                .orElseThrow( () -> new ItemNotFoundException("Root category not found!") );

        List<Category> categoryList = new LinkedList<>();
        fillListRecursively(categoryList, root, "");

        return categoryList;
    }

    private void fillListRecursively(List<Category> categoryList, Category element, String space) {

        element.setName( space + element.getName() );
        categoryList.add(element);

        for (Category child : element.getChildren()) {
            fillListRecursively(categoryList, child, space + "   ");
        }
    }

    @Transactional
    public void adjustIds(Category parent, Category category) {
        long newElId;
        long adjustStartId;
        long adjustEndId;
        long changeValue;

        // new category
        if ( category.getParent() == null ) {
            adjustStartId = getNextBigInOrderId(parent);
            adjustEndId = categoryRepository.count();
            changeValue = 1;

            newElId = adjustStartId;
        }
        else {
            newElId = 10;
            adjustStartId = 0;
            adjustEndId = 0;
            changeValue = 1;
        }

        List<Category> categoryList = categoryRepository.findAllByOrderIdBetween(adjustStartId, adjustEndId);
        categoryList.forEach( cat -> cat.setId( cat.getOrderId() + changeValue ));
        categoryRepository.saveAll(categoryList);

        category.setOrderId(newElId);
        category.setParent(parent);
        parent.getChildren().add(category);
        categoryRepository.save(category);
        categoryRepository.save(parent);
    }


    /**
     * @return next sibling or bigger element's id   / 0 if this is last element
     */
    private long getNextBigInOrderId(Category category) {

        if (category.getChildren().size() > 0) {
            return category.getChildren().stream()
                    .max( Comparator.comparingLong(Category::getOrderId) )
                    .get().getOrderId() + 1;
        }

        return category.getOrderId() + 1;
    }

    public Category findById(Long id) {
        return categoryRepository.findByOrderId(id)
                .orElseThrow( () -> new ItemNotFoundException("No such category!") );
    }


    @Transactional
    public void postAttribute(Long id, CategoryAttribute attribute, List<String> enumValues) {
        Category category = findById(id);
        List<String> names = category.getCategoryAttributes().stream()
                .map( attr -> attr.getName() )
                .toList();
        if (names.contains(attribute.getName())) {
            throw new ItemAlreadyExistsException("There can't be 2 attributes with the same names!");
        }

        attribute.setCategory(category);
        attribute = categoryAttributeRepository.save(attribute);

        if (!attribute.isNumber()) {
            PseudoEnum pseudoEnum = createEnumObject(enumValues);
            attribute.setPseudoEnum(pseudoEnum);
        }

        category.getCategoryAttributes().add(attribute);
        categoryRepository.save(category);
    }

    public PseudoEnum createEnumObject(List<String> enumValues) {
        PseudoEnum pseudoEnum = new PseudoEnum();

        for (int i = 1; i <= enumValues.size(); i++ ) {
            String value = enumValues.get(i - 1);
            pseudoEnum.getValues().add( new PseudoEnumValue( i, pseudoEnum ,value ) );
        }

        pseudoEnum = pseudoEnumRepository.save(pseudoEnum);

        return pseudoEnum;
    }

    @Transactional
    public void deleteAttribute(Long id) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No such attribute!") );

        List<Category> categoryList = categoryRepository.findAllByCategoryAttributesContaining(attribute);
        categoryList.forEach( category -> category.getCategoryAttributes().remove(attribute) );

        categoryRepository.saveAll(categoryList);
        categoryAttributeRepository.delete(attribute);
    }
}
