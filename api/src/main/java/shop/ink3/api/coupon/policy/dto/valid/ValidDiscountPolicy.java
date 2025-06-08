package shop.ink3.api.coupon.policy.dto.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DiscountPolicyValidator.class)
@Target({ ElementType.TYPE }) // DTO 전체에 적용
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDiscountPolicy {
    String message() default "할인 타입에 따른 필수 값이 누락되었거나 잘못 입력되었습니다.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

