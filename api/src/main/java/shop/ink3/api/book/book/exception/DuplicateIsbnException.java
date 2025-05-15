package shop.ink3.api.book.book.exception;

public class DuplicateIsbnException extends RuntimeException {
    public DuplicateIsbnException(String isbn) {
        super("This ISBN is already registered: " + isbn);
    }
}