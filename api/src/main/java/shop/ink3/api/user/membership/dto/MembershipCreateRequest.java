package shop.ink3.api.user.membership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MembershipCreateRequest(
        @NotBlank
        String name,

        @NotNull
        Integer conditionAmount,

        @NotNull
        Integer pointRate
) {
}
