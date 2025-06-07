package shop.ink3.api.user.membership.dto;

public record MembershipStatisticsResponse(
        Long totalCount,
        Long activeCount,
        Long inactiveCount
) {
    public static MembershipStatisticsResponse of(Long totalCount, Long activeCount) {
        long total = totalCount != null ? totalCount : 0L;
        long active = activeCount != null ? activeCount : 0L;
        return new MembershipStatisticsResponse(total, active, total - active);
    }
}
