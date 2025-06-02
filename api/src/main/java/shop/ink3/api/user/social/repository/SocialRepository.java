package shop.ink3.api.user.social.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.social.entity.Social;

public interface SocialRepository extends JpaRepository<Social, Long> {
    /****
     * Retrieves a social entity by provider and provider user ID, eagerly fetching the associated user.
     *
     * @param provider the name of the social authentication provider
     * @param providerUserId the unique identifier assigned by the provider
     * @return an Optional containing the matching Social entity if found, or empty if not found
     */
    @EntityGraph(attributePaths = {"user"})
    Optional<Social> findByProviderAndProviderId(String provider, String providerUserId);
}
