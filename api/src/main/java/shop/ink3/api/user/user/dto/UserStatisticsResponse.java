package shop.ink3.api.user.user.dto;

public record UserStatisticsResponse(
        Long totalUsers,
        Long activeUsers,
        Long dormantUsers,
        Long withdrawnUsers
) {
}
