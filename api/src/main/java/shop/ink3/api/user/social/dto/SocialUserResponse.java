package shop.ink3.api.user.social.dto;

import shop.ink3.api.user.social.entity.Social;

public record SocialUserResponse(
        String provider,
        String providerId,
        Long id,
        String username
) {
    /**
     * Creates a {@code SocialUserResponse} from a given {@code Social} entity.
     *
     * Extracts the provider, provider ID, user ID, and username from the provided {@code Social} object and its associated user.
     *
     * @param social the {@code Social} entity to convert
     * @return a new {@code SocialUserResponse} containing the extracted information
     */
    public static SocialUserResponse from(Social social) {
        return new SocialUserResponse(
                social.getProvider(),
                social.getProviderId(),
                social.getUser().getId(),
                social.getUser().getLoginId()
        );
    }
}
