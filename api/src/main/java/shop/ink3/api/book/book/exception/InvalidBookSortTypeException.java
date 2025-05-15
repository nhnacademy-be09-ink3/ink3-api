package shop.ink3.api.book.book.exception;

public class InvalidBookSortTypeException extends RuntimeException {
    public InvalidBookSortTypeException(String value) {
        super("Invalid sort type: " + value);
    }
}