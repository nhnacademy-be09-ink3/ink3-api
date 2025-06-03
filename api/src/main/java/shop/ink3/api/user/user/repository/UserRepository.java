package shop.ink3.api.user.user.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    Optional<User> findByLoginId(String loginId);

    List<User> findAllByBirthday(LocalDate birthday);

//    @Query("""
//            SELECT new shop.ink3.api.user.user.dto.UserListItemDto(
//                u.id, u.name, u.loginId, u.email, u.phone,
//                u.createdAt, u.lastLoginAt, u.status, m.name, u.point, s.provider
//            )
//            FROM User u
//            LEFT JOIN u.membership m
//            LEFT JOIN Social s ON s.user = u
//            WHERE (:keyword IS NULL OR u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.loginId LIKE %:keyword%)
//            """)
//    Page<UserListItemDto> findUsersForManagement(String keyword, Pageable pageable);
}
