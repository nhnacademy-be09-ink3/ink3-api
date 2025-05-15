package shop.ink3.api.book.book.exception;

public class AladinBookNotFoundException extends RuntimeException {
    public AladinBookNotFoundException(String isbn) {
        super("Book not found in Aladin API. ISBN: " + isbn);
    }
}