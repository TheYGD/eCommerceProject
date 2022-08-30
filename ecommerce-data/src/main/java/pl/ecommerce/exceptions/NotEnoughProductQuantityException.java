package pl.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotEnoughProductQuantityException extends RuntimeException {

    public NotEnoughProductQuantityException(String message) {
        super(message);
    }
}
