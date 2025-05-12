package shop.ink3.api.user.address.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class AddressNotFoundException extends NotFoundException {
    public AddressNotFoundException(long addressId) {
        super("Address not found. ID: %d".formatted(addressId));
    }
}
