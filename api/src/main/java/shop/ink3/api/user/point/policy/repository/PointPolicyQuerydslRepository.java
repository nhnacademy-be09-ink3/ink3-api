package shop.ink3.api.user.point.policy.repository;

import shop.ink3.api.user.point.policy.dto.PointPolicyStatisticsResponse;

public interface PointPolicyQuerydslRepository {
    PointPolicyStatisticsResponse getPointPolicyStatistics();
}
