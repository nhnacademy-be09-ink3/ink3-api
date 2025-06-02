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
    /**
     * Creates a {@code ReviewListResponse} instance from a given {@code Review} entity.
     *
     * @param review the {@code Review} entity to convert
     * @return a {@code ReviewListResponse} containing data extracted from the provided review
     */
    public static ReviewListResponse from(Review review) {
        return new ReviewListResponse(review.getId(), review.getUser().getId(), review.getOrderBook().getId(), review.getUser().getName(),
            review.getTitle(), review.getContent(), review.getRating(), review.getCreatedAt(), review.getModifiedAt());
    }
}

