package shop.ink3.api.review.dto;

public record ReviewUpdateRequest(
    String title,
    String content,
    int rating
) {
}
