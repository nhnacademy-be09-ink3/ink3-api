package shop.ink3.api.user.admin.dto;

import shop.ink3.api.user.admin.entity.Admin;

public record AdminAuthResponse(
        Long id,
        String username,
        String password
) {
    public static AdminAuthResponse from(Admin admin) {
        return new AdminAuthResponse(admin.getId(), admin.getLoginId(), admin.getPassword());
    }
}
