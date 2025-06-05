package shop.ink3.api.review.review.dto;

import java.time.LocalDateTime;
import java.util.List;

import shop.ink3.api.review.review.entity.Review;
import shop.ink3.api.review.reviewImage.dto.ReviewImageResponse;

public record ReviewListResponse(
    Long id,
    Long userId,
    Long bookId,
    Long orderBookId,
    String userName,
    String title,
    String content,
    int rating,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    List<ReviewImageResponse> images
) {
    public static ReviewListResponse from(Review review, List<String> imageUrls) {
        List<ReviewImageResponse> images = imageUrls.stream()
            .map(ReviewImageResponse::from)
            .toList();

        return new ReviewListResponse(
            review.getId(),
            review.getUser().getId(),
            review.getOrderBook().getBook().getId(),
            review.getOrderBook().getId(),
            review.getUser().getName(),
            review.getTitle(),
            review.getContent(),
            review.getRating(),
            review.getCreatedAt(),
            review.getModifiedAt(),
            images
        );
    }
}

