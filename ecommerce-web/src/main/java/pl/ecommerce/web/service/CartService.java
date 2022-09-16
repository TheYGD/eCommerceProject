package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
@Slf4j
public class CartService {

    @Value("${pl.ecommerce.carthash.name}")
    private String CART_COOKIE;
    private final CartRepository cartRepository;
    private final AvailableProductRepository availableProductRepository;
    private final ProductInCartRepository productInCartRepository;
    private final CartToExpireRepository cartToExpireRepository;


    /**
     * Changes ProductInCart's quantity
     * @param userCredentials might be null, credentials of logged user
     * @param id id of product to be deleted
     * @param quantity new quantity
     * @param request HttpServletRequest for not logged users
     * @param response HttpServletResponse for not logged users
     */
    public void changeProductsQuantity(UserCredentials userCredentials, Long id, Integer quantity,
                                       HttpServletRequest request, HttpServletResponse response) {
        AvailableProduct availableProduct = getAvailableProduct(id);

        if (!isQuantityAvailable(availableProduct, quantity)) {
            throw new InvalidArgumentException("Quantity must be between %d and %d"
                    .formatted(1, availableProduct.getQuantity()));
        }

        Cart cart = getCartWithoutReload(userCredentials, request, response);
        ProductInCart productInCart = getProductInCart(cart, availableProduct);

        productInCart.setQuantity(quantity);

        productInCartRepository.save(productInCart);
    }


    /**
     * Removes product form cart
     * @param userCredentials might be null, credentials of logged user
     * @param id id of product to be deleted
     * @param request HttpServletRequest for not logged users
     * @param response HttpServletResponse for not logged users
     */
    @Transactional
    public void removeProduct(UserCredentials userCredentials, Long id, HttpServletRequest request,
                              HttpServletResponse response) {
        Cart cart = getCartWithoutReload(userCredentials, request, response);
        ProductInCart productInCart = getProductInCart(cart, getAvailableProduct(id));

        cart.getProductList().remove(productInCart);
        productInCartRepository.delete(productInCart);
        cartRepository.save(cart);
    }


    /**
     * @param cart cart to be search
     * @param availableProduct product we search for
     * @return instance of ProductInCart representing given availableProduct in given cart
     */
    private ProductInCart getProductInCart(Cart cart, AvailableProduct availableProduct) {
        ProductInCart productInCart = cart.getProductList().stream()
                .filter( product -> product.getProduct().equals(availableProduct.getProduct()) )
                .findFirst()
                .orElseThrow( () -> new ItemNotFoundException("No such product in cart!") );

        return productInCart;
    }


    /**
     * It adds product to cart, it's invoked from controller
     * @param userCredentials might be null, credentials of logged user
     * @param productId id of product to be added
     * @param quantity quantity of product
     * @param request HttpServletRequest for not logged users
     * @param response HttpServletResponse for not logged users
     */
    public void addProductToCart(UserCredentials userCredentials, Long productId, Integer quantity,
                                   HttpServletRequest request, HttpServletResponse response) {

        Optional<AvailableProduct> productOptional = availableProductRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new InvalidArgumentException("Error! Try again later.");
        }

        Cart cart = getCartWithoutReload(userCredentials, request, response);

        addProduct(cart, productOptional.get(), quantity);
    }


    /**
     * It adds product with given quantity to cart
     * @param cart destinated cart
     * @param availableProduct product to be added
     * @param quantity quantity of product
     */
    @Transactional
    public void addProduct(Cart cart, AvailableProduct availableProduct, Integer quantity) {

        if (quantity < 1) {
            throw new InvalidArgumentException("Product quantity must be greater than 0!");
        }

        if (availableProduct.getProduct().getSeller().equals(cart.getOwner())) {
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


    /**
     * @return if given quantity for the product is valid - is between 1 and available quantity
     */
    private boolean isQuantityAvailable(AvailableProduct availableProduct, int quantity) {
        return quantity >= 1 && quantity <= availableProduct.getQuantity();
    }


    /**
     * @return available product by id
     */
    public AvailableProduct getAvailableProduct(Long id) {
        return availableProductRepository.findById(id)
                .orElseThrow( () -> {
                    String message = "Couldn't find product with id: %d!".formatted(id);
                    log.error(message);
                    return new ItemNotFoundException(message);
                });
    }


    public Cart getCartWithReload(UserCredentials userCredentials, HttpServletRequest request,
                                  HttpServletResponse response) {
        Cart cart = getCartWithoutReload(userCredentials, request, response);
        adjustProductsInCartQuantities(cart);

        return cart;
    }


    /**
     * <b>Use when the correct quantities of products inside the cart are not necessary</b> <br/>
     * It returns cart for both logged and unlogged users, creates one if needed. The cart's products quantities are not
     * being adjusted
     * @param userCredentials might be null, if not, it returns cart associated with the given user
     * @param request used to obtain cart from cookie - if exists - for unlogged users
     * @param response used to save the cart cookie after cart creation or expiration date change
     * @return cart
     */
    public Cart getCartWithoutReload(UserCredentials userCredentials, HttpServletRequest request,
                                     HttpServletResponse response) {

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
                        renewCartCookie(cookie, response);
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


    /**
     * Get cart if we have credentials not null, so we can omit nulls for request and response
     * @param userCredentials
     * @return cart of logged user
     */
    public Cart getCartLogged(UserCredentials userCredentials) {
        if (userCredentials == null) {
            log.error("UserCredentials cannot be null!");
            throw new RuntimeException("UserCredentials cannot be null!");
        }

        return getCartWithoutReload(userCredentials, null, null);
    }


    /**
     * Merges unlogged cart from cookies with the users cart after login
     * @param userCredentials users credentials that has just logged in
     * @param id id of cart being used while not logged in
     */
    @Transactional
    public void mergeCartsAfterLogin(UserCredentials userCredentials, Long id) {
        Cart mainCart = getCartLogged(userCredentials);

        cartRepository.findById(id)
                .ifPresent( otherCart -> {
                    otherCart.getProductList()
                            .forEach( productInCart -> {
                                // cant add your own products
                                if (productInCart.getProduct().getSeller().getCredentials().equals( userCredentials )) {
                                    return;
                                }

                                Optional<ProductInCart> sameProduct = mainCart.getProductList().stream()
                                        .filter( product -> product.getProduct().equals( productInCart.getProduct() ))
                                        .findFirst();

                                // product is in both carts
                                if (sameProduct.isPresent()) {
                                    ProductInCart ProductInMainCart = sameProduct.get();
                                    ProductInMainCart.setQuantity( ProductInMainCart.getQuantity() + productInCart.getQuantity() );
                                }
                                else { // is not
                                    productInCart.setCart(mainCart);
                                    otherCart.getProductList().remove(productInCart);
                                    productInCartRepository.save(productInCart);
                                }

                            });

                    productInCartRepository.deleteAll(otherCart.getProductList());

                    cartToExpireRepository.deleteByCart(otherCart);
                    cartRepository.delete(otherCart);
                });

        adjustProductsInCartQuantities(mainCart);
    }


    /**
     * It created cart and saves its id to the cookie for unlogged user to let them use the carts
     * @param response used to set a cookie
     * @return cart associated with the cookie
     */
    private Cart createCartCookie(HttpServletResponse response) {
        Cart cart = cartRepository.save(new Cart());
        LocalDate expirationDate = LocalDate.now().plusDays(8); // to achieve full 7 days - we only use date
        cartToExpireRepository.save( new CartToExpire(cart, expirationDate) );
        Cookie cartCookie = new Cookie(CART_COOKIE, String.valueOf(cart.getId()));
        cartCookie.setPath("/");
        cartCookie.setMaxAge(7 * 24 * 60 * 60); // a week
        response.addCookie(cartCookie);
        response.addCookie(cartCookie);
        response.addCookie(cartCookie);
        response.addCookie(cartCookie);

        return cart;
    }


    /**
     * It changes cookie's expiration date to 7 days
     * @param cookie
     * @param response
     */
    private void renewCartCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setMaxAge(7 * 24 * 60 * 60); // a week
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    /**
     * It changes cart justChanged flag to false, so that the information about changes made to the cart is only
     * displayed once
     * @param cart cart which we want to modify
     */
    public void markJustChangedCartAsFalse(Cart cart) {
        if (!cart.isJustChangedCart()) {
            return;
        }

        cart.setJustChangedCart(false);
        cartRepository.save(cart);
    }


    /**
     * It checks if the products in cart quantity is greater than the available quality, if so, it sets the products in
     * cart quantity to max available quantity
     * @param cart cart in which we want to do the adjustment
     */
    public void adjustProductsInCartQuantities(Cart cart) {
        for (var product : cart.getProductList()) {
            if ( product.getQuantity() > product.getAvailableProduct().getQuantity() ) {
                product.setQuantity( product.getAvailableProduct().getQuantity() );
                cart.setJustChangedCart(true);
            }
        }

        cartRepository.save(cart);
    }
}
