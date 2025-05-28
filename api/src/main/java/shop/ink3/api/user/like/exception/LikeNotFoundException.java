package shop.ink3.api.user.like.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class LikeNotFoundException extends NotFoundException {
    public LikeNotFoundException(long likeId) {
        super("Like not found. ID: %d".formatted(likeId));
    }
}
