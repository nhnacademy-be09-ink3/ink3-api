package shop.ink3.api.user.user.dto;

import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

public record UserAuthResponse(
        Long id,
        String username,
        String password,
        UserStatus status
) {
    public static UserAuthResponse from(User user) {
        return new UserAuthResponse(
                user.getId(),
                user.getLoginId(),
                user.getPassword(),
                user.getStatus()
        );
    }
}
