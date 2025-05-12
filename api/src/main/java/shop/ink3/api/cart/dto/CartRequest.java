package shop.ink3.api.cart.dto;

import shop.ink3.api.cart.entity.Cart;

public record CartRequest(
    Long userId,
    Long bookId,
    int quantity
) {
    public static Cart toEntity(CartRequest request) {
        return new Cart(request.userId, request.bookId, request.quantity);
    }
}
