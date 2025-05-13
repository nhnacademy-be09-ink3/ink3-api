package shop.ink3.api.order.cart.dto;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.order.cart.entity.Cart;
import shop.ink3.api.user.user.entity.User;

public record CartResponse(
    Long id,
    User user,
    Book book,
    int quantity
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(cart.getId(), cart.getUser(), cart.getBook(), cart.getQuantity());
    }
}
