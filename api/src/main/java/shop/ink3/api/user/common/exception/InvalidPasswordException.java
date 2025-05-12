package shop.ink3.api.user.common.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("The current password is incorrect.");
    }
}

