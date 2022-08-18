package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.entity.Cart;
import pl.ecommerce.data.entity.Product;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CartRepository;
import pl.ecommerce.repository.ProductRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;


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
    public String addProductToCart(UserCredentials userCredentials, Long productId) {

        if (productRepository.findById(productId).isEmpty()) {
            return "Error! Try again later.";
        }

        Product product = productRepository.findById(productId).get();
        Cart cart = cartRepository.findByOwner(userCredentials.getUserAccount())
                .orElseThrow( () -> new RuntimeException("s") );

        if (cart.getProductList().contains(product)) {
            return "Product already in cart!";
        }

        cart.getProductList().add(product);
        cartRepository.save(cart);


        return "Product added.";
    }
}
