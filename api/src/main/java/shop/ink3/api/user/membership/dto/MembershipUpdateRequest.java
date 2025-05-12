package shop.ink3.api.user.membership.dto;

public record MembershipUpdateRequest(
        String name,
        Integer conditionAmount,
        Integer pointRate
) {
}
