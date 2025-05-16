package shop.ink3.api.book.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TooManyCategoriesException extends RuntimeException {
    public TooManyCategoriesException(int count) {
        super("A book can belong to at most 10 categories. Given: " + count);
    }
}