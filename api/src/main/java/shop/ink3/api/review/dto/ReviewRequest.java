package shop.ink3.api.review.dto;

import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.review.entity.Review;
import shop.ink3.api.user.user.entity.User;

public record ReviewRequest(
    User user,
    OrderBook orderBook,
    String title,
    String content,
    int rating
) {
    public static Review toEntity(ReviewRequest request) {
        return new Review(request.user, request.orderBook, request.title, request.content, request.rating);
    }
}
