package shop.ink3.api.order.cart.dto;

public record MeCartRequest(
    Long bookId,
    int quantity
) {
}
