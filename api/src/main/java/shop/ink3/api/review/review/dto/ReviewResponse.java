package shop.ink3.api.review.review.dto;

import java.time.LocalDateTime;
import java.util.List;

import shop.ink3.api.review.review.entity.Review;

public record ReviewResponse(
    Long id,
    Long userId,
    Long bookId,
    Long orderBookId,
    String title,
    String content,
    int rating,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    List<String> images,
    String description
) {
    public static ReviewResponse from(Review review, List<String> imageUrls) {
        return new ReviewResponse(
            review.getId(),
            review.getUser().getId(),
            review.getOrderBook().getBook().getId(),
            review.getOrderBook().getId(),
            review.getTitle(),
            review.getContent(),
            review.getRating(),
            review.getCreatedAt(),
            review.getModifiedAt(),
            imageUrls,
            null
        );
    }

    public static ReviewResponse from(Review review, List<String> imageUrls, String description) {
        return new ReviewResponse(
            review.getId(),
            review.getUser().getId(),
            review.getOrderBook().getBook().getId(),
            review.getOrderBook().getId(),
            review.getTitle(),
            review.getContent(),
            review.getRating(),
            review.getCreatedAt(),
            review.getModifiedAt(),
            imageUrls,
            description
        );
    }
}
