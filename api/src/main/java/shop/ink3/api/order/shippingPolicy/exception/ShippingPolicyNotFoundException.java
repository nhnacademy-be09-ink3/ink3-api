package shop.ink3.api.order.shippingPolicy.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class ShippingPolicyNotFoundException extends NotFoundException {
    public ShippingPolicyNotFoundException() {
        super("Available ShippingPolicy not found.");
    }

    public ShippingPolicyNotFoundException(long shippingPolicyId) {
        super("ShippingPolicy not found. ID: %d".formatted(shippingPolicyId));
    }

}
