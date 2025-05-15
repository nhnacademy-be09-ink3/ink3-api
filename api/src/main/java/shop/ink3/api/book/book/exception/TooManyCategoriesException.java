package shop.ink3.api.book.book.exception;

public class TooManyCategoriesException extends RuntimeException {
    public TooManyCategoriesException(int count) {
        super("A book can belong to at most 10 categories. Given: " + count);
    }
}