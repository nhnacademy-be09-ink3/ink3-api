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
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    /**
     * Creates a ReviewResponse instance from a Review entity by mapping its fields and related entity identifiers.
     *
     * @param review the Review entity to convert
     * @return a ReviewResponse containing data extracted from the given Review
     */
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getId(), review.getUser().getId(), review.getOrderBook().getId(),
            review.getTitle(), review.getContent(), review.getRating(), review.getCreatedAt(), review.getModifiedAt());
    }
}
