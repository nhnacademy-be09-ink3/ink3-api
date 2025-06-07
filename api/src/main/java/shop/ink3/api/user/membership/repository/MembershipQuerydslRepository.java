package shop.ink3.api.user.membership.repository;

import shop.ink3.api.user.membership.dto.MembershipStatisticsResponse;

public interface MembershipQuerydslRepository {
    MembershipStatisticsResponse getMembershipStatistics();
}
