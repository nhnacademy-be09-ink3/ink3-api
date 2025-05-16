package shop.ink3.api.order.order.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException() {
        super("Available Order not found.");
    }

    public OrderNotFoundException(long orderId) {
        super("Order not found. ID: %d".formatted(orderId));
    }

}
