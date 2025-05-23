package shop.ink3.api.order.refund.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class RefundNotFoundException extends NotFoundException {
    public RefundNotFoundException() {
        super("Available Refund not found.");
    }

    public RefundNotFoundException(long refundId) {
        super("Refund not found. ID: %d".formatted(refundId));
    }
}
