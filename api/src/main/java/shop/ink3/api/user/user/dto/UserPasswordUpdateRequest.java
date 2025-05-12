package shop.ink3.api.user.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {
}
