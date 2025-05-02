package shop.ink3.api.user.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberStatus {
    ACTIVE("활성"),
    DORMANT("휴면"),
    WITHDRAWN("탈퇴");

    private final String label;
}
