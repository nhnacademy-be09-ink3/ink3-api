package shop.ink3.api.user.common.exception;

public class WithdrawnException extends RuntimeException {
    public WithdrawnException(String loginId) {
        super("This account has been withdrawn. Login Id: %s".formatted(loginId));
    }
}
