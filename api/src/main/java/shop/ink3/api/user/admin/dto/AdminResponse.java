package shop.ink3.api.user.admin.dto;

import java.time.LocalDateTime;
import shop.ink3.api.user.admin.entity.Admin;
import shop.ink3.api.user.admin.entity.AdminStatus;

public record AdminResponse(
        Long id,
        String loginId,
        String name,
        AdminStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    public static AdminResponse from(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getLoginId(),
                admin.getName(),
                admin.getStatus(),
                admin.getLastLoginAt(),
                admin.getCreatedAt()
        );
    }
}
