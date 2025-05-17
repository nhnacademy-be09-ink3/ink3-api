package shop.ink3.api.coupon.policy.dto.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.entity.DiscountType;

public class DiscountPolicyValidator implements ConstraintValidator<ValidDiscountPolicy, PolicyCreateRequest> {

    @Override
    public boolean isValid(PolicyCreateRequest request, ConstraintValidatorContext context) {
        if (request == null) return true; // null일 경우는 다른 @NotNull로 처리하도록

        boolean valid = true;

        if (request.discountType() == DiscountType.FIXED) {
            valid = request.discount_value() > 0 && request.discount_percentage() == 0;
        } else if (request.discountType() == DiscountType.RATE) {
            valid = request.discount_percentage() > 0 && request.discount_value() == 0;
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("할인 타입에 맞는 필드를 정확히 입력해주세요.")
                    .addConstraintViolation();
        }

        return valid;
    }
}

