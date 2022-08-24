package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.other.ProductSort;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.ProductRepository;

import java.util.List;

@Service
public class CategoryService {

    @Value("${pl.ecommerce.records_on_page}")
    private int RECORDS_ON_PAGE;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSort productSort;

    public CategoryService(CategoryRepository categoryRepository, ProductSort productSort,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productSort = productSort;
    }


    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("Category with id=%d doesn't exist!".formatted(id)) );
    }

    public Page<Product> getProductList(Category category, int pageNr, int sortOption) {
        Pageable pageable = PageRequest.of(pageNr - 1, RECORDS_ON_PAGE, productSort.getSort(sortOption));
        return productRepository.findAllByCategory(category, pageable);
    }
}
