package shop.ink3.api.coupon.coupon.dto;

import shop.ink3.api.coupon.coupon.entity.IssueType;

public record CouponIssueRequest(
        Long userId,
        IssueType issueType, // all_user일 경우 batch서버로 전송
        Long bookId,       // BOOK 쿠폰일 경우
        Long categoryId    // CATEGORY 쿠폰일 경우
) {}

