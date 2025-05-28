package shop.ink3.api.review.review.dto;

import java.time.LocalDateTime;
import java.util.List;

import shop.ink3.api.review.review.entity.Review;

public record ReviewResponse(
    Long id,
    Long userId,
    Long orderBookId,
    String title,
    String content,
    int rating,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    List<String> images
) {
    public static ReviewResponse from(Review review, List<String> imageUrls) {
        return new ReviewResponse(
            review.getId(),
            review.getUser().getId(),
            review.getOrderBook().getId(),
            review.getTitle(),
            review.getContent(),
            review.getRating(),
            review.getCreatedAt(),
            review.getModifiedAt(),
            imageUrls
        );
    }
}
