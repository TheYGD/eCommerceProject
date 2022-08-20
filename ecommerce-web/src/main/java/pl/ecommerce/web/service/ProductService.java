package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import pl.ecommerce.data.entity.*;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.ProductRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CartService cartService;


    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No item found with id=" + id) );
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByQuery(String query) {
        return productRepository.findAll();
    }


    /**
     * @return message if added successfully, product already in cart or error
     */
    public String addProductToCart(UserCredentials userCredentials, Long productId, Integer quantity,
                                   HttpServletRequest request, HttpServletResponse response) {

        return cartService.addProductToCart(userCredentials, productId, quantity, request, response);
    }

}
