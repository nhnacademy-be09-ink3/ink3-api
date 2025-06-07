package shop.ink3.api.user.user.dto;

import shop.ink3.api.user.user.entity.UserStatus;

public record UserStatusResponse(
        UserStatus status
) {
}
