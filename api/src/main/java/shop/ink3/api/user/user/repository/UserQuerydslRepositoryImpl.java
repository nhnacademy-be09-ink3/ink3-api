package shop.ink3.api.user.user.repository;

import static shop.ink3.api.user.membership.entity.QMembership.membership;
import static shop.ink3.api.user.social.entity.QSocial.social;
import static shop.ink3.api.user.user.entity.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;
import shop.ink3.api.user.user.dto.UserListItemDto;
import shop.ink3.api.user.user.entity.User;

public class UserQuerydslRepositoryImpl extends QuerydslRepositorySupport implements UserQuerydslRepository {
    public UserQuerydslRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Page<UserListItemDto> getUsersForManagement(String keyword, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();
        if (StringUtils.hasText(keyword)) {
            condition.and(user.name.containsIgnoreCase(keyword)
                    .or(user.loginId.containsIgnoreCase(keyword))
                    .or(user.email.containsIgnoreCase(keyword)));
        }

        List<UserListItemDto> content = from(user)
                .leftJoin(user.membership, membership)
                .leftJoin(social).on(social.user.eq(user))
                .where(condition)
                .orderBy(user.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(Projections.constructor(UserListItemDto.class,
                        user.id,
                        user.name,
                        user.loginId,
                        user.email,
                        user.phone,
                        user.createdAt,
                        user.lastLoginAt,
                        user.status,
                        membership.name,
                        user.point,
                        social.provider
                )).fetch();

        Long total = from(user)
                .leftJoin(user.membership, membership)
                .leftJoin(social).on(social.user.eq(user))
                .where(condition)
                .select(user.count())
                .fetchOne();

        total = total == null ? 0L : total;

        return new PageImpl<>(content, pageable, total);
    }
}
