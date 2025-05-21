package shop.ink3.api.coupon.policy.dto.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.entity.DiscountType;

public class DiscountPolicyValidator implements ConstraintValidator<ValidDiscountPolicy, PolicyCreateRequest> {

    @Override
    public boolean isValid(PolicyCreateRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        DiscountType discountType = request.discountType();
        Integer discountValue = request.discountValue();
        Integer discountPercentage = request.discountPercentage();

        boolean valid = true;

        if (discountType == DiscountType.FIXED) {
            valid = (discountValue != null && discountValue > 0) &&
                    (discountPercentage != null && discountPercentage == 0);
        } else if (discountType == DiscountType.RATE) {
            valid = (discountPercentage != null && discountPercentage > 0) &&
                    (discountValue != null && discountValue == 0);
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("할인 타입에 맞는 필드를 정확히 입력해주세요.")
                    .addConstraintViolation();
        }

        return valid;
    }

}

