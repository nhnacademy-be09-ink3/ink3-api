package shop.ink3.api.book.book.exception;


public class AladinParsingException extends RuntimeException {
    public AladinParsingException(Throwable cause) {
        super("Failed to parse the response from Aladin API.", cause);
    }
}