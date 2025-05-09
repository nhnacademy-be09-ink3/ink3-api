package shop.ink3.api.user.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserStatus {
    ACTIVE("활성"),
    DORMANT("휴면"),
    WITHDRAWN("탈퇴");

    private final String label;
}
