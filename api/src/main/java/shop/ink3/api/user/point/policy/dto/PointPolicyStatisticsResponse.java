package shop.ink3.api.user.point.policy.dto;

public record PointPolicyStatisticsResponse(
        Long totalCount,
        Long activeCount,
        Long inactiveCount
) {
    public static PointPolicyStatisticsResponse of(Long totalCount, Long activeCount) {
        long total = totalCount != null ? totalCount : 0L;
        long active = activeCount != null ? activeCount : 0L;
        return new PointPolicyStatisticsResponse(total, active, total - active);
    }
}
