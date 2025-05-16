package shop.ink3.api.payment.dto;

import java.time.LocalDateTime;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.payment.entity.PaymentType;

public record TossPaymentResponse (
        String paymentKey,
        int totalAmount,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt
){

}
