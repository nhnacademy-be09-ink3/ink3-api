package shop.ink3.api.order.cart.dto;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.order.cart.entity.Cart;
import shop.ink3.api.user.user.entity.User;

public record CartRequest(
    User user,
    Book book,
    int quantity
) {
    public static Cart toEntity(CartRequest request) {
        return new Cart(request.user, request.book, request.quantity);
    }
}
