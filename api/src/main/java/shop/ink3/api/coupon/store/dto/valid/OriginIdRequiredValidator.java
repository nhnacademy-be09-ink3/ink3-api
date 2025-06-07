package shop.ink3.api.coupon.store.dto.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import shop.ink3.api.coupon.store.dto.CouponIssueRequest;
import shop.ink3.api.coupon.store.entity.OriginType;

public class OriginIdRequiredValidator implements ConstraintValidator<ValidOriginId, CouponIssueRequest> {

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
