package shop.ink3.api.order.packaging.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class PackagingNotFoundException extends NotFoundException {
    public PackagingNotFoundException() {
        super("Available Packaging not found.");
    }

    public PackagingNotFoundException(long packagingId) {
        super("Packaging not found. ID: %d".formatted(packagingId));
    }

}
