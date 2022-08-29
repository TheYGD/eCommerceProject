package pl.ecommerce.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.other.ProductSort;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.AvailableProductRepository;
import pl.ecommerce.repository.UserRepository;

@Service
public class SellerService {

    @Value("${pl.ecommerce.products-on-page}")
    private int RECORDS_ON_PAGE;
    private final UserRepository userRepository;
    private final AvailableProductRepository availableProductRepository;
    private final ProductSort productSort;

    public SellerService(UserRepository userRepository, AvailableProductRepository availableProductRepository, ProductSort productSort) {
        this.userRepository = userRepository;
        this.availableProductRepository = availableProductRepository;
        this.productSort = productSort;
    }



    public User getSeller(Long id) {
        return userRepository.findById(id)
                .orElseThrow( () -> {
                    return new ItemNotFoundException("Seller with id %d not found!".formatted(id));
                });
    }

    public Page<AvailableProduct> getSellerProducts(User seller, int pageNr, int sortOption) {
        Pageable pageable = PageRequest.of(pageNr - 1, RECORDS_ON_PAGE, productSort.getSort(sortOption));
        return availableProductRepository.findAllBySeller(seller, pageable);
    }
}
