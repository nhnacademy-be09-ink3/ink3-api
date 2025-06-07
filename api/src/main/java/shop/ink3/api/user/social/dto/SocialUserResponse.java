package shop.ink3.api.user.social.dto;

import shop.ink3.api.user.social.entity.Social;

public record SocialUserResponse(
        String provider,
        String providerId,
        Long id,
        String username
) {
    public static SocialUserResponse from(Social social) {
        return new SocialUserResponse(
                social.getProvider(),
                social.getProviderId(),
                social.getUser().getId(),
                social.getUser().getLoginId()
        );
    }
}
