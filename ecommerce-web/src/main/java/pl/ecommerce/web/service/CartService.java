package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {

    private final String CART_COOKIE = "CART_HASH";

    private final CartRepository cartRepository;
    private final AvailableProductRepository availableProductRepository;
    private final ProductInCartRepository productInCartRepository;
    private final CartToExpireRepository cartToExpireRepository;


    public void changeProductsQuantity(UserCredentials userCredentials, Long id, Integer quantity,
                                       HttpServletRequest request, HttpServletResponse response) {
        AvailableProduct availableProduct = getProduct(id);

        if (!isQuantityAvailable(availableProduct, quantity)) {
            throw new InvalidArgumentException("Quantity must be between %d and %d"
                    .formatted(1, availableProduct.getQuantity()));
        }

        Cart cart = getCart(userCredentials, request, response);
        ProductInCart productInCart = getProductInCart(cart, availableProduct);

        productInCart.setQuantity(quantity);

        productInCartRepository.save(productInCart);
    }


    @Transactional
    public void removeProduct(UserCredentials userCredentials, Long id, HttpServletRequest request,
                              HttpServletResponse response) {
        Cart cart = getCart(userCredentials, request, response);
        ProductInCart productInCart = getProductInCart(cart, getProduct(id));

        cart.getProductList().remove(productInCart);
        productInCartRepository.delete(productInCart);
        cartRepository.save(cart);
    }


    public ProductInCart getProductInCart(Cart cart, AvailableProduct availableProduct) {
        ProductInCart productInCart = cart.getProductList().stream()
                .filter( product -> product.getProduct().equals(availableProduct.getProduct()) )
                .findFirst()
                .orElseThrow( () -> new ItemNotFoundException("No such product in cart!") );

        return productInCart;
    }


    /**
     * @return message if added successfully, product already in cart or error
     */
    public void addProductToCart(UserCredentials userCredentials, Long productId, Integer quantity,
                                   HttpServletRequest request, HttpServletResponse response) {

        Optional<AvailableProduct> productOptional = availableProductRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new InvalidArgumentException("Error! Try again later.");
        }

        Cart cart = getCart(userCredentials, request, response);

        addProduct(cart, productOptional.get(), quantity, userCredentials);
    }

    @Transactional
    public void addProduct(Cart cart, AvailableProduct availableProduct, Integer quantity, UserCredentials userCredentials) {

        if (quantity < 1) {
            throw new InvalidArgumentException("Product quantity must be greater than 0!");
        }

        if (availableProduct.getProduct().getSeller().getCredentials().equals(userCredentials)) {
            throw new InvalidArgumentException("Can't add your own products to cart!");
        }

        Optional<ProductInCart> productInCartOptional = cart.getProductList().stream()
                .filter(product1 -> product1.getProduct().equals(availableProduct.getProduct()))
                .findFirst();

        if (productInCartOptional.isPresent()) {
            ProductInCart productInCart = productInCartOptional.get();
            int newQuantity = productInCart.getQuantity() + quantity;
            if (!isQuantityAvailable(availableProduct, newQuantity)) {
                throw new InvalidArgumentException("Product's quantity is not enough!");
            }

            productInCart.setQuantity( newQuantity );
            productInCartRepository.save(productInCart);
        }

        else {
            ProductInCart productInCart = new ProductInCart(cart, availableProduct, quantity);
            if (!isQuantityAvailable(availableProduct, quantity)) {
                throw new InvalidArgumentException("Product's quantity is not enough!");
            }

            cart.getProductList().add(productInCart);
            cartRepository.save(cart);
        }
    }


    private boolean isQuantityAvailable(AvailableProduct availableProduct, int quantity) {
        return quantity >= 1 && quantity <= availableProduct.getQuantity();
    }


    public AvailableProduct getProduct(Long id) {
        return availableProductRepository.findById(id)
                .orElseThrow( () -> {
                    String message = "Couldn't find product with id: %d!".formatted(id);
                    log.error(message);
                    return new ItemNotFoundException(message);
                });
    }


    public Cart getCart(UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) {

        if (userCredentials == null) {

            if (request.getCookies() != null) {
                Optional<Cookie> cookieOptional = Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals(CART_COOKIE))
                        .findFirst();

                if (cookieOptional.isPresent()) {
                    Cookie cookie = cookieOptional.get();
                    String cartValue = cookie.getValue();
                    Long cartHash = Long.valueOf(cartValue); // todo edit cart id to actual cart hash

                    Optional<Cart> cartOptional = cartRepository.findById(cartHash);
                    if (cartOptional.isPresent()) {
                        return cartOptional.get();
                    }
                    // else continue, create new cart
                }
            }

            // crate cart for non-logged in users
            return createCartCookie(response);
        }

        return cartRepository.findByOwnerId(userCredentials.getUserAccount().getId())
                .orElseThrow( () -> {
                    log.error("Account with email %s is not associated with a cart!"
                            .formatted(userCredentials.getEmail()));
                    return new ItemNotFoundException("No cart found!");
                });
    }

    public Cart getCartLogged(UserCredentials userCredentials) {
        if (userCredentials == null) {
            log.error("UserCredentials cannot be null!");
            throw new RuntimeException("USER_CREDENTIALS CANNOT BE NULL");
        }

        return getCart(userCredentials, null, null);
    }


    @Transactional
    public void mergeCartsAfterLogin(UserCredentials userCredentials, Long id) {
        Cart mainCart = getCartLogged(userCredentials);

        cartRepository.findById(id)
                .ifPresent( otherCart -> {
                    otherCart.getProductList()
                            .forEach( productInCart -> {
                                addProduct(mainCart, productInCart.getAvailableProduct(), productInCart.getQuantity(),
                                        userCredentials);
                                }
                            );

                    productInCartRepository.deleteAll(otherCart.getProductList());

                    cartToExpireRepository.deleteByCart(otherCart);
                    cartRepository.delete(otherCart);
                });
    }


    private Cart createCartCookie(HttpServletResponse response) {
        Cart cart = cartRepository.save(new Cart());
        LocalDate expirationDate = LocalDate.now().plusDays(8); // to achieve full 7 days - we only use date
        cartToExpireRepository.save( new CartToExpire(cart, expirationDate) );
        Cookie cartCookie = new Cookie(CART_COOKIE, String.valueOf(cart.getId()));
        cartCookie.setPath("/");
        cartCookie.setMaxAge(7 * 24 * 60 * 60); // a week
        response.addCookie(cartCookie);

        return cart;
    }

    public boolean isCartEmpty(Cart cart) {
        return cart.getProductList().size() == 0;
    }

    public void markJustDeletedProductsAsFalse(Cart cart) {
        if (!cart.isJustDeletedProducts()) {
            return;
        }

        cart.setJustDeletedProducts(false);
        cartRepository.save(cart);
    }
}
