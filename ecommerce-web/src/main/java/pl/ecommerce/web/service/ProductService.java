package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.entity.Cart;
import pl.ecommerce.data.entity.Product;
import pl.ecommerce.data.entity.ProductInCart;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CartRepository;
import pl.ecommerce.repository.ProductInCartRepository;
import pl.ecommerce.repository.ProductRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductInCartRepository productInCartRepository;
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
    @Transactional
    public String addProductToCart(UserCredentials userCredentials, Long productId, Integer quanity) {


        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return "Error! Try again later.";
        }

        Cart cart = cartRepository.findByOwner(userCredentials.getUserAccount())
                .orElseThrow( () -> {
                    log.error("Account with email %s is not associated with a cart!"
                        .formatted(userCredentials.getEmail()));
                    return new ItemNotFoundException("No cart found!");
                });

        Optional<ProductInCart> productInCartOptional = cart.getProductList().stream()
                .filter(product -> product.getProduct().equals(productOptional.get()))
                .findFirst();

        if (productInCartOptional.isPresent()) {
            ProductInCart productInCart = productInCartOptional.get();
            productInCart.setQuantity(productInCart.getQuantity() + quanity );
            productInCartRepository.save(productInCart);
        }

        else {
            ProductInCart productInCart = new ProductInCart(cart, productOptional.get(), quanity);
            productInCart = productInCartRepository.save(productInCart);
            cart.getProductList().add(productInCart);
            cartRepository.save(cart);
        }

        return "Product added.";
    }
}
