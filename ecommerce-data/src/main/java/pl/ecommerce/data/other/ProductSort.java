package pl.ecommerce.data.other;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.ecommerce.data.domain.SortOption;

@Component
public class ProductSort {

    public Sort getSort(int option) {
        SortOption sortOption = SortOption.getById(option);

        return switch (sortOption) {
            case PRICE_LOWEST_FIRST -> Sort.by("product.price");
            case PRICE_HIGHEST_FIRST -> Sort.by("product.price").descending();
            default -> Sort.by("product");
        };
    }
}
