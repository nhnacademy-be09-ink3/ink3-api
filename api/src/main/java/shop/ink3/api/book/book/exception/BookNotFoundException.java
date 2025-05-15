package shop.ink3.api.book.book.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("Book not found. ID: %d".formatted(bookId));
    }
}
