package shop.ink3.api.order.guest.dto;

import java.time.LocalDateTime;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;

public record GuestOrderResponse(
        Long orderId,
        String orderUUID,
        OrderStatus orderStatus,
        LocalDateTime orderedAt,
        String ordererName,
        String ordererPhone
) {
    public static GuestOrderResponse from(Order guestOrder){
        return new GuestOrderResponse(
                guestOrder.getId(),
                guestOrder.getOrderUUID(),
                guestOrder.getStatus(),
                guestOrder.getOrderedAt(),
                guestOrder.getOrdererName(),
                guestOrder.getOrdererPhone()
        );
    }
}
