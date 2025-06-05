package shop.ink3.api.order.guest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.payment.entity.PaymentType;

public interface GuestOrderDetailsResponse {
    Long getOrderId();
    String getOrderUUId();
    OrderStatus getStatus();
    LocalDateTime getOrderedAt();
    String getOrdererName();
    String getOrdererPhone();
    LocalDate getPreferredDeliveryDate();
    LocalDateTime getDeliveredAt();
    String getRecipientName();
    String getRecipientPhone();
    Integer getPostalCode();
    String getDefaultAddress();
    String getExtraAddress();
    Integer getShippingFee();
    String getShippingCode();
    Integer getPaymentAmount();
    PaymentType getPaymentType();
    LocalDateTime getRequestedAt();
    LocalDateTime getApprovedAt();
}
