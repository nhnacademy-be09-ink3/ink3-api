package shop.ink3.api.user.user.dto;

import java.time.LocalDateTime;
import shop.ink3.api.user.user.entity.UserStatus;

public record UserListItemDto(
        Long id,
        String name,
        String loginId,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt,
        UserStatus status,
        String membership,
        Integer point,
        String social
) {
}
