package shop.ink3.api.user.user.exception;

public class InsufficientPointException extends RuntimeException {
    public InsufficientPointException() {
        super("Insufficient points to complete this action.");
    }
}
