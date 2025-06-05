package shop.ink3.api.order.guest.dto;

import lombok.Builder;
import shop.ink3.api.payment.entity.PaymentType;

@Builder
public record GuestPaymentConfirmRequest(
        Long orderId,
        String paymentKey,
        String orderUUID,
        Integer amount,
        PaymentType paymentType
){
}
