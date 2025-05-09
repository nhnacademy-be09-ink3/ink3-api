package shop.ink3.api.user.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AdminStatus {
    ACTIVE,
    DORMANT,
    WITHDRAWN
}
