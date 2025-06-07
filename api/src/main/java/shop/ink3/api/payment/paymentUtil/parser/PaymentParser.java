package shop.ink3.api.payment.paymentUtil.parser;

import shop.ink3.api.order.guest.dto.GuestPaymentConfirmRequest;
import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.entity.Payment;

public interface PaymentParser {
    Payment paymentResponseParser(PaymentConfirmRequest paymentConfirmRequest, String json);
    Payment paymentResponseParser(GuestPaymentConfirmRequest paymentConfirmRequest, String json);
}
