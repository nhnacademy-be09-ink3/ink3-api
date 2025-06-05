package shop.ink3.api.order.guest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.order.entity.OrderStatus;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GuestOrderDetailsResponse {
    private Long orderId;
    private Long orderUUId;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private String ordererName;
    private String ordererPhone;
    private LocalDate preferredDeliveryDate;
    private LocalDateTime deliveredAt;
    private String recipientName;
    private String recipientPhone;
    private Integer postalCode;
    private String defaultAddress;
    private String extraAddress;
    private Integer shippingFee;
    private String shippingCode;
}
