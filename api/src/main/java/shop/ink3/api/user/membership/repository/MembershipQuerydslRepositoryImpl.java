package shop.ink3.api.user.membership.repository;

import static shop.ink3.api.user.membership.entity.QMembership.membership;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import shop.ink3.api.user.membership.dto.MembershipStatisticsResponse;
import shop.ink3.api.user.membership.entity.Membership;

public class MembershipQuerydslRepositoryImpl
        extends QuerydslRepositorySupport implements MembershipQuerydslRepository {
    public MembershipQuerydslRepositoryImpl() {
        super(Membership.class);
    }

    @Override
    public MembershipStatisticsResponse getMembershipStatistics() {
        Tuple result = from(membership)
                .select(
                        membership.count(),
                        new CaseBuilder().when(membership.isActive.eq(true)).then(1L).otherwise(0L).sumLong()
                ).fetchOne();

        if (result == null) {
            return new MembershipStatisticsResponse(0L, 0L, 0L);
        }

        Long total = result.get(0, Long.class);
        Long active = result.get(1, Long.class);

        return MembershipStatisticsResponse.of(total, active);
    }
}
