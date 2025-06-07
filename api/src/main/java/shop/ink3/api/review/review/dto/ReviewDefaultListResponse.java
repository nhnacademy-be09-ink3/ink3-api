package shop.ink3.api.review.review.dto;

import java.time.LocalDateTime;

import shop.ink3.api.review.review.entity.Review;

public record ReviewDefaultListResponse(
    Long id,
    Long userId,
    Long bookId,
    Long orderBookId,
    String userName,
    String title,
    String content,
    int rating,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    public static ReviewDefaultListResponse from(Review review) {
        return new ReviewDefaultListResponse(review.getId(), review.getUser().getId(), review.getOrderBook().getBook().getId(), review.getOrderBook().getId(), review.getUser().getName(),
            review.getTitle(), review.getContent(), review.getRating(), review.getCreatedAt(), review.getModifiedAt());
    }
}

