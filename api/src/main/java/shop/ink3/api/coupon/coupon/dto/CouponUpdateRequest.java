package shop.ink3.api.coupon.coupon.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public record CouponUpdateRequest(

        @NotBlank(message = "쿠폰 이름은 필수입니다.")
        String name,

        String code,  // REDEEM 타입일 경우 필수

        @NotNull(message = "쿠폰 정책 ID는 필수입니다.")
        Long policyId,

        @NotNull(message = "트리거 타입은 필수입니다.")
        TriggerType triggerType,

        @NotNull(message = "발급 유형은 필수입니다.")
        IssueType issueType,

        Boolean active,

        Integer validDays  // null 가능: 정책 default 따름
) {}

