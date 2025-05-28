package shop.ink3.api.review.reviewImage.dto;

import shop.ink3.api.review.reviewImage.entity.ReviewImage;

public record ReviewImageResponse(
    String imageUrl
) {
    public static ReviewImageResponse from(String imageUrl) {
        return new ReviewImageResponse(imageUrl);
    }

    public static ReviewImageResponse from(ReviewImage image) {
        return new ReviewImageResponse(image.getImageUrl());
    }
}
