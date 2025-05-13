package shop.ink3.api.user.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserMembershipUpdateRequest(
        @NotNull Long membershipId
) {
}
