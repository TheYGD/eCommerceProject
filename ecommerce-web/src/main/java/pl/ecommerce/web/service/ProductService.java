package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.other.ProductSort;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.ProductRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Value("${pl.ecommerce.records_on_page}")
    private int RECORDS_ON_PAGE;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ProductSort productSort;

    public ProductService(ProductRepository productRepository, CartService cartService, ProductSort productSort) {
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.productSort = productSort;
    }



    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No item found with id=" + id) );
    }

    public Page<Product> findAll(int pageNr, int sortOption) {
        Pageable pageable = PageRequest.of(pageNr - 1, RECORDS_ON_PAGE, productSort.getSort(sortOption));
        return productRepository.findAll(pageable);
    }

    public Page<Product> findByQuery(String query, int pageNr, int sortOption) {
        Pageable pageable = PageRequest.of(pageNr - 1, RECORDS_ON_PAGE, productSort.getSort(sortOption));
        return productRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);
    }


    /**
     * @return message if added successfully, product already in cart or error
     */
    public String addProductToCart(UserCredentials userCredentials, Long productId, Integer quantity,
                                   HttpServletRequest request, HttpServletResponse response) {

        return cartService.addProductToCart(userCredentials, productId, quantity, request, response);
    }


    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow( () -> {
                    String message = "Could not find product with id: %d!".formatted(id);
                    log.error(message);
                    return new ItemNotFoundException(message);
                });
    }
}
