package shop.ink3.api.user.admin.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class AdminNotFoundException extends NotFoundException {
    public AdminNotFoundException(long adminId) {
        super("Admin not found. ID: %d".formatted(adminId));
    }
}
