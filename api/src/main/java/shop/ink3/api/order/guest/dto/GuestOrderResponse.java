package shop.ink3.api.order.guest.dto;

import java.time.LocalDateTime;
import shop.ink3.api.order.guest.entiity.GuestOrder;
import shop.ink3.api.order.order.entity.OrderStatus;

public record GuestOrderResponse(
        Long id,
        Long orderId,
        OrderStatus orderStatus,
        LocalDateTime orderedAt,
        String ordererName,
        String ordererPhone
) {
    public static GuestOrderResponse from(GuestOrder guestOrder){
        return new GuestOrderResponse(
                guestOrder.getId(),
                guestOrder.getOrder().getId(),
                guestOrder.getOrder().getStatus(),
                guestOrder.getOrder().getOrderedAt(),
                guestOrder.getOrder().getOrdererName(),
                guestOrder.getOrder().getOrdererPhone()
        );
    }
}
