package shop.ink3.api.coupon.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public record CouponCreateRequest(
        @NotNull Long policyId,
        @NotBlank String name,
        @NotNull IssueType issueType,
        @NotNull LocalDateTime issuableFrom,
        @NotNull LocalDateTime expiresAt,

        TriggerType triggerType,

        List<Long> bookIdList,
        List<Long> categoryIdList
) { }
