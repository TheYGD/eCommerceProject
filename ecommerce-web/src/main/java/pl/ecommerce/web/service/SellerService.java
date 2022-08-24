package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.other.ProductSort;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;

import java.util.List;

@Service
public class SellerService {

    @Value("${pl.ecommerce.records_on_page}")
    private int RECORDS_ON_PAGE;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSort productSort;

    public SellerService( UserRepository userRepository, ProductRepository productRepository, ProductSort productSort) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productSort = productSort;
    }



    public User getSeller(Long id) {
        return userRepository.findById(id)
                .orElseThrow( () -> {
                    return new ItemNotFoundException("Seller with id %d not found!".formatted(id));
                });
    }

    public Page<Product> getSellerProducts(User seller, int pageNr, int sortOption) {
        Pageable pageable = PageRequest.of(pageNr - 1, RECORDS_ON_PAGE, productSort.getSort(sortOption));
        return productRepository.findAllBySeller(seller, pageable);
    }
}