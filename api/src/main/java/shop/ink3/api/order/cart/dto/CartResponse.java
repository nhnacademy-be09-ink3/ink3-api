package shop.ink3.api.order.cart.dto;

import shop.ink3.api.order.cart.entity.Cart;

public record CartResponse(
    Long id,
    Long userId,
    Long bookId,
    int quantity
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(cart.getId(), cart.getUser().getId(), cart.getBook().getId(), cart.getQuantity());
    }
}
