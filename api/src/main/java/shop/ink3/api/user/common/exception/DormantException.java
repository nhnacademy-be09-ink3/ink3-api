package shop.ink3.api.user.common.exception;

public class DormantException extends RuntimeException {
    public DormantException(long id) {
        super("This account is dormant. ID: %d".formatted(id));
    }
}
