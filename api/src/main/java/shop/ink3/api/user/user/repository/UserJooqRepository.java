package shop.ink3.api.user.user.repository;

import static org.jooq.tables.Memberships.MEMBERSHIPS;
import static org.jooq.tables.Socials.SOCIALS;
import static org.jooq.tables.Users.USERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.ink3.api.user.user.dto.UserListItemDto;
import shop.ink3.api.user.user.entity.UserStatus;


@RequiredArgsConstructor
@Repository
public class UserJooqRepository {
    private final DSLContext dsl;

    public Page<UserListItemDto> getUsersForManagementOrderByUserIdAsc(String keyword, Pageable pageable) {
        Condition condition = DSL.trueCondition();
        if (StringUtils.hasText(keyword)) {
            condition = condition.and(
                    USERS.NAME.containsIgnoreCase(keyword)
                            .or(USERS.EMAIL.containsIgnoreCase(keyword))
                            .or(USERS.LOGIN_ID.containsIgnoreCase(keyword))
            );
        }

        List<UserListItemDto> content = dsl.select(
                        USERS.ID,
                        USERS.NAME,
                        USERS.LOGIN_ID,
                        USERS.EMAIL,
                        USERS.PHONE,
                        USERS.CREATED_AT,
                        USERS.LAST_LOGIN_AT,
                        USERS.STATUS,
                        MEMBERSHIPS.NAME,
                        USERS.POINT,
                        SOCIALS.PROVIDER
                ).from(USERS)
                .leftJoin(MEMBERSHIPS).on(USERS.MEMBERSHIP_ID.eq(MEMBERSHIPS.ID))
                .leftJoin(SOCIALS).on(USERS.ID.eq(SOCIALS.USER_ID))
                .where(condition)
                .orderBy(USERS.ID.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch(record -> new UserListItemDto(
                        record.get(USERS.ID),
                        record.get(USERS.NAME),
                        record.get(USERS.LOGIN_ID),
                        record.get(USERS.EMAIL),
                        record.get(USERS.PHONE),
                        record.get(USERS.CREATED_AT),
                        record.get(USERS.LAST_LOGIN_AT),
                        UserStatus.valueOf(record.get(USERS.STATUS)),
                        record.get(MEMBERSHIPS.NAME),
                        record.get(USERS.POINT),
                        record.get(SOCIALS.PROVIDER)
                ));

        Long total = dsl.selectCount()
                .from(USERS)
                .leftJoin(MEMBERSHIPS).on(USERS.MEMBERSHIP_ID.eq(MEMBERSHIPS.ID))
                .leftJoin(SOCIALS).on(USERS.ID.eq(SOCIALS.USER_ID))
                .where(condition)
                .fetchOne(0, Long.class);

        total = total == null ? 0L : total;

        return new PageImpl<>(content, pageable, total);
    }
}
