package shop.ink3.api.book.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCategorySelectionException  extends RuntimeException {
    public InvalidCategorySelectionException (String message) {
        super(message);
    }
}