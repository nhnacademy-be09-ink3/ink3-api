package shop.ink3.api.book.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AladinBookNotFoundException extends RuntimeException {
    public AladinBookNotFoundException(String isbn) {
        super("Book not found in Aladin API. ISBN: " + isbn);
    }
}