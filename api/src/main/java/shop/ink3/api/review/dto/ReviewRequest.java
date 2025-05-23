package shop.ink3.api.review.dto;

public record ReviewRequest(
    Long userId,
    Long orderBookId,
    String title,
    String content,
    int rating
) {
}
