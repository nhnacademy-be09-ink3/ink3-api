package shop.ink3.api.user.common.exception;

public class WithdrawnException extends RuntimeException {
    public WithdrawnException(long id) {
        super("This account has been withdrawn. ID: %d".formatted(id));
    }
}
