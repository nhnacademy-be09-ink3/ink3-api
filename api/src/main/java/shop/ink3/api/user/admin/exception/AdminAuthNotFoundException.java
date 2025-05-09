package shop.ink3.api.user.admin.exception;

public class AdminAuthNotFoundException extends RuntimeException {
    public AdminAuthNotFoundException(String loginId) {
        super("Admin for authentication not found. Login ID: %s".formatted(loginId));
    }
}
