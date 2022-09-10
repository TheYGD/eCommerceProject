package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.UserRepository;

import java.util.Map;

@Service
@AllArgsConstructor
public class SellerService {


    private final ProductService productService;
    private final UserRepository userRepository;


    public User getSeller(Long id) {
        return userRepository.findById(id)
                .orElseThrow( () -> {
                    return new ItemNotFoundException("Seller with id %d not found!".formatted(id));
                });
    }

    public Category getCategory(Long id) {
        return productService.getCategory(id);
    }

    public Page<AvailableProduct> findProducts(Category category, String query, int pageNr, int sortOption,
                                               String price, Map<String, String> otherValues, Long sellerId) {
        return productService.findProducts(category, query, pageNr, sortOption, price, otherValues, sellerId);
    }
}
