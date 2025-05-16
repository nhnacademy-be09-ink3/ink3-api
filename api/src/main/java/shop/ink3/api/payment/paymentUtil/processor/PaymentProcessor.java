package shop.ink3.api.payment.paymentUtil.processor;

import shop.ink3.api.payment.dto.PaymentConfirmRequest;

public interface PaymentProcessor {
    String processPayment(PaymentConfirmRequest request);
}
