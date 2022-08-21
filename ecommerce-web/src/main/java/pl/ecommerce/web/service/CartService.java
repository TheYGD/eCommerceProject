package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.exceptions.InvalidQuantityException;
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
    private final ProductRepository productRepository;
    private final ProductInCartRepository productInCartRepository;
    private final CartToExpireRepository cartToExpireRepository;
    private final ProductWithQuantityRepository productWithQuantityRepository;


    public void changeProductsQuantity(UserCredentials userCredentials, Long id, Integer quantity,
                                       HttpServletRequest request, HttpServletResponse response) {
        if (quantity < 1 || quantity > 9999) { // todo max is product in stock
            throw new InvalidQuantityException("Quantity must be between %d - %d".formatted(1, 999));
        }

        Cart cart = getCart(userCredentials, request, response);
        ProductInCart productInCart = getProductInCart(cart, id);

        productInCart.getProductWithQuantity().setQuantity(quantity);

        productInCartRepository.save(productInCart);
    }


    @Transactional
    public void removeProduct(UserCredentials userCredentials, Long id, HttpServletRequest request
            , HttpServletResponse response) {
        Cart cart = getCart(userCredentials, request, response);
        ProductInCart productInCart = getProductInCart(cart, id);

        cart.getProductList().remove(productInCart);
        productInCartRepository.delete(productInCart);
        cartRepository.save(cart);
    }


    public ProductInCart getProductInCart(Cart cart, Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No such product found!") );

        ProductInCart productInCart = cart.getProductList().stream()
                .filter( prod -> prod.getProductWithQuantity().getProduct().equals(product) )
                .findFirst()
                .orElseThrow( () -> new ItemNotFoundException("No such product in cart!") );

        return productInCart;
    }


    /**
     * @return message if added successfully, product already in cart or error
     */
    @Transactional
    public String addProductToCart(UserCredentials userCredentials, Long productId, Integer quantity,
                                   HttpServletRequest request, HttpServletResponse response) {

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return "Error! Try again later.";
        }

        Cart cart = getCart(userCredentials, request, response);

        return addProduct(cart, productOptional.get(), quantity);
    }

    public String addProduct(Cart cart, Product product, Integer quantity) {

        Optional<ProductInCart> productInCartOptional = cart.getProductList().stream()
                .filter(product1 -> product1.getProductWithQuantity().getProduct().equals(product))
                .findFirst();

        if (productInCartOptional.isPresent()) {
            ProductInCart productInCart = productInCartOptional.get();
            productInCart.getProductWithQuantity().setQuantity(productInCart.getProductWithQuantity().getQuantity() + quantity );
            productInCartRepository.save(productInCart);
        }

        else {
            ProductWithQuantity productWithQuantity = new ProductWithQuantity(product, quantity);
            productWithQuantity = productWithQuantityRepository.save(productWithQuantity);
            ProductInCart productInCart = new ProductInCart(cart, productWithQuantity);
            productInCart = productInCartRepository.save(productInCart);
            cart.getProductList().add(productInCart);
            cartRepository.save(cart);
        }

        return "Product added.";
    }

    @Transactional
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
                            .forEach( productInCart -> addProduct(mainCart,
                                    productInCart.getProductWithQuantity().getProduct(),
                                    productInCart.getProductWithQuantity().getQuantity()) );

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
}
