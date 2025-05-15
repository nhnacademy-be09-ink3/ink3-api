package shop.ink3.api.coupon.coupon.dto;

import shop.ink3.api.coupon.coupon.entity.TriggerType;

public record CouponIssueMessage(
        Long userId,
        TriggerType triggerType,
        Long bookId,       // BOOK 쿠폰일 경우
        Long categoryId    // CATEGORY 쿠폰일 경우
) {}

