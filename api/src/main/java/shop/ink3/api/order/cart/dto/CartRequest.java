package shop.ink3.api.order.cart.dto;

public record CartRequest(
    Long userId,
    Long bookId,
    int quantity
) {
}
