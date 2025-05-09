package shop.ink3.api.review.review.dto;

import java.time.LocalDateTime;

import shop.ink3.api.review.review.entity.Review;

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
        return new ReviewResponse(review.getId(), review.getUserId(), review.getOrderBookId(), review.getTitle(),
            review.getContent(), review.getRating(), review.getCreatedAt());
    }
}
