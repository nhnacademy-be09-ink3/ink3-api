package shop.ink3.api.user.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

public record UserResponse(
        Long id,
        String loginId,
        String name,
        String email,
        String phone,
        LocalDate birthday,
        Integer point,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthday(),
                user.getPoint(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }
}
