package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public User getSeller(Long id) {
        return userRepository.findById(id)
                .orElseThrow( () -> {
                    return new ItemNotFoundException("Seller with id %d not found!".formatted(id));
                });
    }

    public List<Product> getSellerProducts(User seller) {
        return productRepository.findAllBySeller(seller);
    }
}
