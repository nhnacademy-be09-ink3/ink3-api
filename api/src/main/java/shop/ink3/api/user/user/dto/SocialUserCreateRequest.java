package shop.ink3.api.user.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SocialUserCreateRequest(
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotNull LocalDate birthday,
        @NotBlank String provider,
        @NotBlank String providerId
) {
}
