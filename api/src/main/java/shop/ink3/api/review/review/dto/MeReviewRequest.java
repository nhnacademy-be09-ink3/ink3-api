package shop.ink3.api.review.review.dto;

import jakarta.validation.constraints.*;

public record MeReviewRequest(
    @NotNull
    Long orderBookId,

    @NotBlank
    String title,

    @NotBlank
    String content,

    @Min(1) @Max(5)
    int rating
) {
}
