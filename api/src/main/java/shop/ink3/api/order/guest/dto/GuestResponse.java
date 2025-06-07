package shop.ink3.api.order.guest.dto;

import java.time.LocalDateTime;
import shop.ink3.api.order.guest.entiity.Guest;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;

public record GuestResponse(
        Long guestId,
        Long orderId,
        String email
) {
    public static GuestResponse from(Guest guest){
        return new GuestResponse(
                guest.getId(),
                guest.getOrder().getId(),
                guest.getEmail()
        );
    }
}
