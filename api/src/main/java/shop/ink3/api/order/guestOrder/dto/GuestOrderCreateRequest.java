package shop.ink3.api.order.guestOrder.dto;

public record GuestOrderCreateRequest(
        Long orderId,
        String email,
        String password
) {

}
