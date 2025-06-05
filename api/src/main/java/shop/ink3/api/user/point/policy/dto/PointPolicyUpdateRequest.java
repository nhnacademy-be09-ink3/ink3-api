package shop.ink3.api.user.point.policy.dto;

public record PointPolicyUpdateRequest(
        String name,
        Integer join_point,
        Integer review_point,
        Integer default_rate
) {
}
