package shop.ink3.api.book.common.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(long bookId) {
        super("존재하지 않는 도서입니다 id: %d".formatted(bookId));
    }
}
