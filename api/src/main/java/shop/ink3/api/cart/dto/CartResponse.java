package shop.ink3.api.cart.dto;

import shop.ink3.api.cart.entity.Cart;

public record CartResponse(
    Long id,
    Long userId,
    Long bookId,
    int quantity
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(cart.getId(), cart.getUserId(), cart.getBookId(), cart.getQuantity());
    }
}
