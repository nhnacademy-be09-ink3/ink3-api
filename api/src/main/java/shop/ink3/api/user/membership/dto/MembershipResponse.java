package shop.ink3.api.user.membership.dto;

import java.time.LocalDateTime;
import shop.ink3.api.user.membership.entity.Membership;

public record MembershipResponse(
        Long id,
        String name,
        Integer conditionAmount,
        Integer pointRate,
        Boolean isActive,
        Boolean isDefault,
        LocalDateTime createdAt
) {
    public static MembershipResponse from(Membership membership) {
        return new MembershipResponse(
                membership.getId(),
                membership.getName(),
                membership.getConditionAmount(),
                membership.getPointRate(),
                membership.getIsActive(),
                membership.getIsDefault(),
                membership.getCreatedAt()
        );
    }
}
