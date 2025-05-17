package shop.ink3.api.coupon.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public record CouponCreateRequest(
        @NotNull Long policyId,
        @NotBlank String couponName,
        @NotNull TriggerType triggerType,
        @NotNull IssueType issueType,
        String CouponCode,
        @NotNull LocalDateTime issueDate,
        @NotNull LocalDateTime expirationDate,

        List<Long> bookIdList,
        List<Long> categoryIdList

) { }
