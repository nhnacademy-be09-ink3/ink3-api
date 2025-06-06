package shop.ink3.api.user.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminPasswordUpdateRequest(
        @NotBlank
        String currentPassword,

        @NotBlank
        String newPassword
) {
}
