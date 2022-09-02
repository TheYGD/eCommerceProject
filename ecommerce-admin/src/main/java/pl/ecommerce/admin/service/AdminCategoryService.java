package pl.ecommerce.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.CategoryDto;
import pl.ecommerce.data.dto.PseudoEnumDto;
import pl.ecommerce.data.mapper.CategoryMapper;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.exceptions.ItemAlreadyExistsException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.ProductAttributeRepository;
import pl.ecommerce.repository.PseudoEnumRepository;
import pl.ecommerce.repository.PseudoEnumValueRepository;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@AllArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductAttributeRepository productAttributeRepository;
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

            newElId = adjustStartId != 0 ? adjustStartId : parent.getId() + 1;
        }
        else {
            newElId = 10;
            adjustStartId = 0;
            adjustEndId = 0;
            changeValue = 1;
        }

        List<Category> categoryList = categoryRepository.findAllByOrderIdBetween(adjustStartId, adjustEndId);
        categoryList.forEach( cat -> cat.setId( cat.getId() + changeValue ));
        categoryRepository.saveAll(categoryList);

        category.setId(newElId);
        category.setParent(parent);
        parent.getChildren().add(category);
        categoryRepository.save(category);
        categoryRepository.save(parent);
    }


    /**
     * @return next sibling or bigger element's id   / 0 if this is last element
     */
    private long getNextBigInOrderId(Category category) {
        if (categoryRepository.count() == category.getId()) {
            return 0;
        }

        if (category.getChildren().size() > 0) {
            return category.getChildren().stream()
                    .max( Comparator.comparingLong(Category::getId) )
                    .get().getId();
        }

        return category.getId() + 1;
    }

    public Category findById(Long id) {
        return categoryRepository.findByOrderId(id)
                .orElseThrow( () -> new ItemNotFoundException("No such category!") );
    }


    @Transactional
    public void postAttribute(Long id, ProductAttribute attribute) {
        Category category = findById(id);
        List<String> names = category.getProductAttributes().stream()
                .map( attr -> attr.getName() )
                .toList();
        if (names.contains(attribute.getName())) {
            throw new ItemAlreadyExistsException("There can't be 2 attributes with the same names!");
        }

        attribute = productAttributeRepository.save(attribute);

        category.getProductAttributes().add(attribute);
        categoryRepository.save(category);
    }

    public Long createEnumObject(PseudoEnumDto pseudoEnumDto) {
        PseudoEnum pseudoEnum = new PseudoEnum();

        for (int i = 1; i <= pseudoEnumDto.getValues().size(); i++ ) {
            String value = pseudoEnumDto.getValues().get(i);
            pseudoEnum.getValues().add( new PseudoEnumValue( i, pseudoEnum ,value ) );
        }

        pseudoEnum = pseudoEnumRepository.save(pseudoEnum);

        return pseudoEnum.getId();
    }

    @Transactional
    public void deleteAttribute(Long id) {
        ProductAttribute attribute = productAttributeRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No such attribute!") );

        List<Category> categoryList = categoryRepository.findAllByProductAttributesContaining(attribute);
        categoryList.forEach( category -> category.getProductAttributes().remove(attribute) );

        categoryRepository.saveAll(categoryList);
        productAttributeRepository.delete(attribute);
    }
}
