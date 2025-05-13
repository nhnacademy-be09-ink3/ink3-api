package shop.ink3.api.user.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserStatus {
    ACTIVE,
    DORMANT,
    WITHDRAWN
}
