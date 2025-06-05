package shop.ink3.api.user.point.policy.repository;


import static shop.ink3.api.user.point.policy.entity.QPointPolicy.pointPolicy;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import shop.ink3.api.user.point.policy.dto.PointPolicyStatisticsResponse;
import shop.ink3.api.user.point.policy.entity.PointPolicy;

public class PointPolicyQuerydslRepositoryImpl
        extends QuerydslRepositorySupport implements PointPolicyQuerydslRepository {
    public PointPolicyQuerydslRepositoryImpl() {
        super(PointPolicy.class);
    }

    @Override
    public PointPolicyStatisticsResponse getPointPolicyStatistics() {
        Tuple result = from(pointPolicy)
                .select(
                        pointPolicy.count(),
                        new CaseBuilder().when(pointPolicy.isActive.eq(true)).then(1L).otherwise(0L).sumLong()
                ).fetchOne();

        if (result == null) {
            return new PointPolicyStatisticsResponse(0L, 0L, 0L);
        }

        Long total = result.get(0, Long.class);
        Long active = result.get(1, Long.class);

        return PointPolicyStatisticsResponse.of(total, active);
    }
}
