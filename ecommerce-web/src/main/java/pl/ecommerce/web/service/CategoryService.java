package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.CategoryAttributeDto;
import pl.ecommerce.data.mapper.CategoryAttributeMapper;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.CategoryAttributeRepository;
import pl.ecommerce.repository.PseudoEnumRepository;
import pl.ecommerce.repository.PseudoEnumValueRepository;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final PseudoEnumRepository pseudoEnumRepository;
    private final PseudoEnumValueRepository pseudoEnumValueRepository;



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

    public List<CategoryAttributeDto> getCategoryAttributes(Long id) {
        Category category = categoryRepository.findByOrderId(id)
                .orElseThrow( () -> new ItemNotFoundException("No such category!") );

        return category.getAllCategoryAttributes().stream()
                .map( categoryAttribute -> CategoryAttributeMapper.INSTANCE.toDto(categoryAttribute) )
                .toList();
    }

    public long getCategoryOrderEnd(Category category) {
        if (category.getParent() == null) {
            return categoryRepository.count();
        }

        List<Category> siblings = category.getParent().getChildren().stream()
                .sorted(Comparator.comparingLong(Category::getOrderId))
                .toList();

        int id = siblings.indexOf(category);
        if (siblings.size() > id + 1) {
            return siblings.get(id + 1).getOrderId() - 1;
        }

        return getCategoryOrderEnd(category.getParent());
    }
}
