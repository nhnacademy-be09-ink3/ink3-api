package shop.ink3.api.order.cart.dto;

public record GuestCartRequest(
    Long bookId,
    int quantity
) {
}
