package shop.ink3.api.coupon.policy.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.entity.DiscountType;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PolicyCreateRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void fixedDiscount_validInput_shouldPass() {
        PolicyCreateRequest request = new PolicyCreateRequest(
                "Fixed Discount",
                10000,
                DiscountType.FIXED,
                1000, // value
                0,    // percentage,
                2000
        );

        Set<ConstraintViolation<PolicyCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void rateDiscount_validInput_shouldPass() {
        PolicyCreateRequest request = new PolicyCreateRequest(
                "Rate Discount",
                10000,
                DiscountType.RATE,
                0,    // value
                15,   // percentage
                5000
        );

        Set<ConstraintViolation<PolicyCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void fixedDiscount_withPercentage_shouldFail() {
        PolicyCreateRequest request = new PolicyCreateRequest(
                "Invalid Fixed",
                10000,
                DiscountType.FIXED,
                1000,
                1000,// ❌ 잘못 입력된 percentage
                20000
        );

        Set<ConstraintViolation<PolicyCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("할인 타입에 맞는 필드를 정확히 입력해주세요");
    }

    @Test
    void rateDiscount_withValue_shouldFail() {
        PolicyCreateRequest request = new PolicyCreateRequest(
                "Invalid Rate",
                10000,
                DiscountType.RATE,
                1000, // ❌ 잘못 입력된 value
                10,
                2000
        );

        Set<ConstraintViolation<PolicyCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("할인 타입에 맞는 필드를 정확히 입력해주세요");
    }
}

