package shop.ink3.api.user.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super("User Not Found: %d".formatted(userId));
    }
}
