package shop.ink3.api.coupon.store.dto.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import shop.ink3.api.coupon.store.dto.CouponIssueRequest;
import shop.ink3.api.coupon.store.entity.OriginType;

public class OriginIdRequiredValidator implements ConstraintValidator<ValidOriginId, CouponIssueRequest> {

    /**
     * Validates that the `originId` field is present in a `CouponIssueRequest` when the `originType` is either `BOOK` or `CATEGORY`.
     *
     * Returns {@code true} if the request or its `originType` is {@code null}, deferring null checks to other annotations.
     * If `originType` is `BOOK` or `CATEGORY` and `originId` is {@code null}, adds a custom constraint violation and returns {@code false}.
     *
     * @param value   the `CouponIssueRequest` to validate
     * @param context the context in which the constraint is evaluated
     * @return {@code true} if valid according to the described logic; {@code false} otherwise
     */
    @Override
    public boolean isValid(CouponIssueRequest value, ConstraintValidatorContext context) {
        if (value == null || value.originType() == null) {
            return true; // 다른 @NotNull 어노테이션에서 처리됨
        }

        if ((value.originType() == OriginType.BOOK || value.originType() == OriginType.CATEGORY)
                && value.originId() == null) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("originId는 BOOK 또는 CATEGORY 타입일 경우 필수입니다.")
                    .addPropertyNode("originId")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
