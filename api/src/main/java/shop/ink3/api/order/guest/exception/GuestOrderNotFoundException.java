package shop.ink3.api.order.guest.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class GuestOrderNotFoundException extends NotFoundException {
    public GuestOrderNotFoundException() {
        super("Available GuestOrder not found.");
    }

    public GuestOrderNotFoundException(long orderId) {
        super("GuestOrder not found. ORDER_ID: %d".formatted(orderId));
    }

}
