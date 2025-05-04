package shop.ink3.api.user.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
