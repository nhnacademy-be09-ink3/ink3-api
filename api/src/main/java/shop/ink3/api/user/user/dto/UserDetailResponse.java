package shop.ink3.api.user.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import shop.ink3.api.user.membership.dto.MembershipResponse;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

public record UserDetailResponse(
        Long id,
        String loginId,
        String name,
        String email,
        String phone,
        LocalDate birthday,
        Integer point,
        MembershipResponse membership,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthday(),
                user.getPoint(),
                MembershipResponse.from(user.getMembership()),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }
}
