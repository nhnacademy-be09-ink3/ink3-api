package shop.ink3.api.user.point.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.ink3.api.user.point.history.entity.PointHistoryStatus;

public record PointHistoryCreateRequest(
        @NotNull
        Integer delta,

        @NotNull
        PointHistoryStatus status,

        @NotBlank
        String description
) {
}
