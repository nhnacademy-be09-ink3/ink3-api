package shop.ink3.api.user.membership.dto;

public record MembershipCreateRequest(
        String name,
        Integer conditionAmount,
        Integer pointRate
) {
}
