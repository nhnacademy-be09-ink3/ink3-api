package shop.ink3.api.payment.exception;

import shop.ink3.api.common.exception.AlreadyExistsException;

public class PaymentAlreadyExistsException extends AlreadyExistsException {
    public PaymentAlreadyExistsException() {
        super("Payment Already Exists about this Order.");
    }

    public PaymentAlreadyExistsException(long orderId) {
        super("Payment Already Exists. Order ID: %d".formatted(orderId));
    }

}
