package shop.ink3.api.user.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminCreatedRequest(
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String name
) {
}
