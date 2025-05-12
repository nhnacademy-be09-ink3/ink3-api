package shop.ink3.api.review.dto;

import shop.ink3.api.review.entity.Review;

public record ReviewRequest(
    Long userId,
    Long orderBookId,
    String title,
    String content,
    int rating
) {
    public static Review toEntity(ReviewRequest request) {
        return new Review(request.userId, request.orderBookId, request.title, request.content, request.rating);
    }
}
