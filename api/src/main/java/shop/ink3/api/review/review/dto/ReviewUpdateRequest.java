package shop.ink3.api.review.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewUpdateRequest(
    @NotBlank @Size(max = 50)
    String title,

    @NotBlank
    String content,

    @Min(1) @Max(5)
    int rating
) {
}
