package shop.ink3.api.user.like.dto;

import jakarta.validation.constraints.NotNull;

public record LikeCreateRequest(
        @NotNull Long bookId
) {
}
