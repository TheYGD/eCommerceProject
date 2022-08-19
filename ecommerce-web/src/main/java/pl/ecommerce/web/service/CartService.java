package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.entity.Cart;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CartRepository;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;


    public Cart findByUserCredentials(UserCredentials userCredentials) {
        return cartRepository.findByOwner(userCredentials.getUserAccount())
                .orElseThrow( () -> {
                    log.error("Account with email %s is not associated with a cart!"
                            .formatted(userCredentials.getEmail()));
                    return new ItemNotFoundException("No cart found!");
                });
    }
}
