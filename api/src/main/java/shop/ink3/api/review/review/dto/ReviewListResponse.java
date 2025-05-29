package shop.ink3.api.review.review.dto;

import java.time.LocalDateTime;

import shop.ink3.api.review.review.entity.Review;

public record ReviewListResponse(
    Long id,
    Long userId,
    Long orderBookId,
    String userName,
    String title,
    String content,
    int rating,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    public static ReviewListResponse from(Review review) {
        return new ReviewListResponse(review.getId(), review.getUser().getId(), review.getOrderBook().getId(), review.getUser().getName(),
            review.getTitle(), review.getContent(), review.getRating(), review.getCreatedAt(), review.getModifiedAt());
    }
}

