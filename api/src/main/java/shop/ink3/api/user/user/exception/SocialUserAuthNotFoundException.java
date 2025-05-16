package shop.ink3.api.user.user.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class SocialUserAuthNotFoundException extends NotFoundException {
    public SocialUserAuthNotFoundException(String provider, String providerUserId) {
        super("Social user not found. Provider: %s, Provider User ID: %s".formatted(provider, providerUserId));
    }
}
