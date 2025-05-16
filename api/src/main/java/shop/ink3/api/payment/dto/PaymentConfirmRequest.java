package shop.ink3.api.payment.dto;

import shop.ink3.api.payment.entity.PaymentType;

public record PaymentConfirmRequest (
    long orderId,
    String paymentKey,
    String orderUUID,
    long amount,
    PaymentType paymentType
){

}
