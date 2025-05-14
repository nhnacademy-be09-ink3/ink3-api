package shop.ink3.api.review.dto;

import java.time.LocalDateTime;

import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.review.entity.Review;
import shop.ink3.api.user.user.entity.User;

public record ReviewResponse(
    Long id,
    Long userId,
    Long orderBookId,
    String title,
    String content,
    int rating,
    LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getId(), review.getUser().getId(), review.getOrderBook().getId(), review.getTitle(),
            review.getContent(), review.getRating(), review.getCreatedAt());
    }
}
