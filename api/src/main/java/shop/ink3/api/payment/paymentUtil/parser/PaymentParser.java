package shop.ink3.api.payment.paymentUtil.parser;

import shop.ink3.api.payment.entity.Payment;

public interface PaymentParser {
    Payment paymentResponseParser(long orderId, String json);
}
