package shop.ink3.api.book.book.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class AladinParsingException extends RuntimeException {
    public AladinParsingException(Throwable cause) {
        super("Failed to parse the response from Aladin API.", cause);
    }
}