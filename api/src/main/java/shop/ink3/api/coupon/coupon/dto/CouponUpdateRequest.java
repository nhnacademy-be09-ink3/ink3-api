package shop.ink3.api.coupon.coupon.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import shop.ink3.api.coupon.coupon.entity.IssueType;

public record CouponUpdateRequest(

        @NotBlank(message = "쿠폰 이름은 필수입니다.")
        String name,

        @NotNull(message = "쿠폰 정책 ID는 필수입니다.")
        Long policyId,

        @NotNull(message = "발급 유형은 필수입니다.")
        IssueType issueType
) {}

