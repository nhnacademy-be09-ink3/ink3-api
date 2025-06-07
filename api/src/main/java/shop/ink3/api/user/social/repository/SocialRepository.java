package shop.ink3.api.user.social.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.social.entity.Social;

public interface SocialRepository extends JpaRepository<Social, Long> {
    @EntityGraph(attributePaths = {"user"})
    Optional<Social> findByProviderAndProviderId(String provider, String providerUserId);
}
