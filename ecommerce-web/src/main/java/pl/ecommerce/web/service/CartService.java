package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.entity.Cart;
import pl.ecommerce.data.entity.Product;
import pl.ecommerce.data.entity.ProductInCart;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.exceptions.InvalidQuantityException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CartRepository;
import pl.ecommerce.repository.ProductInCartRepository;
import pl.ecommerce.repository.ProductRepository;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductInCartRepository productInCartRepository;


    public Cart findByUserCredentials(UserCredentials userCredentials) {
        return getCartByUserCredentials(userCredentials);
    }


    public void changeProductsQuantity(UserCredentials userCredentials, Long id, Integer quantity) {
        if (quantity < 1 || quantity > 9999) { // todo max is product in stock
            throw new InvalidQuantityException("Quantity must be between %d - %d".formatted(1, 999));
        }

        Cart cart = getCartByUserCredentials(userCredentials);
        ProductInCart productInCart = getProductInCartByCredentialsAndIdAndCart(userCredentials, id, cart);

        productInCart.setQuantity(quantity);

        productInCartRepository.save(productInCart);
    }


    @Transactional
    public void removeProduct(UserCredentials userCredentials, Long id) {
        Cart cart = getCartByUserCredentials(userCredentials);
        ProductInCart productInCart = getProductInCartByCredentialsAndIdAndCart(userCredentials, id, cart);

        cart.getProductList().remove(productInCart);
        productInCartRepository.delete(productInCart);
        cartRepository.save(cart);
    }



    public Cart getCartByUserCredentials(UserCredentials userCredentials) {
        return cartRepository.findByOwner(userCredentials.getUserAccount())
                .orElseThrow( () -> {
                    log.error("Account with email %s is not associated with a cart!"
                            .formatted(userCredentials.getEmail()));
                    return new ItemNotFoundException("No cart found!");
                });
    }

    public ProductInCart getProductInCartByCredentialsAndIdAndCart(UserCredentials userCredentials, Long id, Cart cart) {
        Product product = productRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No such product found!") );

        ProductInCart productInCart = cart.getProductList().stream()
                .filter( prod -> prod.getProduct().equals(product) )
                .findFirst()
                .orElseThrow( () -> new ItemNotFoundException("No such product in cart!") );

        return productInCart;
    }
}
