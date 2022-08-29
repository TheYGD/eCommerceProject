package pl.ecommerce.data.other;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ProductSort {

    public Sort getSort(int option) {
        Sort sort = switch (option) {
            case 1 -> Sort.by("product.price");

            default -> Sort.by("product.price");
        };

        return sort;
    }
}
