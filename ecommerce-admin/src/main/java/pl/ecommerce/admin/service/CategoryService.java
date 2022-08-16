package pl.ecommerce.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.dto.CategoryDto;
import pl.ecommerce.data.entity.Category;
import pl.ecommerce.data.mapper.CategoryMapper;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.repository.CategoryRepository;

import java.util.LinkedList;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void postCategory(CategoryDto categoryDTO) {
        if (categoryDTO == null || categoryDTO.getName() == null || categoryDTO.getDescription() == null) {
            throw new InvalidArgumentException("Invalid category body!");
        }

        categoryDTO.setProducts(new LinkedList<>());

        Category category = CategoryMapper.INSTANCE.DtoToEntity(categoryDTO);
        categoryRepository.save(category);
    }
}
