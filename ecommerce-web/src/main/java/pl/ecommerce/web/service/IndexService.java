package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.repository.CategoryRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class IndexService {

    private final CategoryRepository categoryRepository;


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

}
