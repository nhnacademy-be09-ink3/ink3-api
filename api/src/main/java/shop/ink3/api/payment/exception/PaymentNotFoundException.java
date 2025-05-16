package shop.ink3.api.payment.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException() {
        super("Available Payment not found.");
    }

    // 주문에 대한 결제 정보가 없음.
    public PaymentNotFoundException(long orderId) {
        super("Payment not found. ID: %d".formatted(orderId));
    }

}
