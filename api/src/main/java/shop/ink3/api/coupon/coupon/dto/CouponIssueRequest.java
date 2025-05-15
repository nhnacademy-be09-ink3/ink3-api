package shop.ink3.api.coupon.coupon.dto;

import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public record CouponIssueRequest(
        Long userId,
        TriggerType triggerType,
        IssueType issueType, // all_user일 경우 batch서버로 전송
        Long bookId,       // BOOK 쿠폰일 경우
        Long categoryId    // CATEGORY 쿠폰일 경우
) {}

