package shop.ink3.api.order.refundPolicy.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class RefundPolicyNotFoundException extends NotFoundException {
    public RefundPolicyNotFoundException() {
        super("Available RefundPolicy not found.");
    }

    public RefundPolicyNotFoundException(long shippingPolicyId) {
        super("RefundPolicy not found. ID: %d".formatted(shippingPolicyId));
    }
}
