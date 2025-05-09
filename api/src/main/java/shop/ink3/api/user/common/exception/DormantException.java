package shop.ink3.api.user.common.exception;

public class DormantException extends RuntimeException {
    public DormantException(String loginId) {
        super("This account is dormant. Login Id: %s".formatted(loginId));
    }
}
