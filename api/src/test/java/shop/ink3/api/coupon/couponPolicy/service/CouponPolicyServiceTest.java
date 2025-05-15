package shop.ink3.api.coupon.couponPolicy.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;
import shop.ink3.api.coupon.policy.exception.PolicyAlreadyExistException;
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;
import shop.ink3.api.coupon.policy.service.PolicyService;

@ExtendWith(MockitoExtension.class)
class CouponPolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    @Test
    void getPolicyById_success() {
        // given
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME10")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .build();

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // when
        PolicyResponse response = policyService.getPolicyById(1L);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PolicyResponse.from(policy, "쿠폰 정책 조회 완료"), response);
    }

    @Test
    void getPolicyById_notFound_throwsException() {
        // given
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(PolicyNotFoundException.class, () -> {
            policyService.getPolicyById(1L);
        });
    }

    @Test
    void getPolicyByName_success() {
        // given
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME10")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .build();

        when(policyRepository.findByName("WELCOME10")).thenReturn(Optional.of(policy));

        // when
        PolicyResponse response = policyService.getPolicyByName("WELCOME10");

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PolicyResponse.from(policy, "쿠폰 정책 조회 완료"), response);
    }

    @Test
    void getPolicyByName_notFound_throwsException() {
        // given
        when(policyRepository.findByName("NOT_EXIST")).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(PolicyNotFoundException.class, () -> {
            policyService.getPolicyByName("NOT_EXIST");
        });
    }

    @Test
    void createPolicy_success() {
        // given
        PolicyCreateRequest request = new PolicyCreateRequest(
                "WELCOME10",
                DiscountType.RATE,
                10000,
                10,
                50,
                50000
        );

        when(policyRepository.existsByName("WELCOME10")).thenReturn(false);

        CouponPolicy savedPolicy = CouponPolicy.builder()
                .id(1L)
                .name(request.name())
                .discountType(request.discountType())
                .minimum_order_amount(request.minimum_order_amount())
                .discount_value(request.discount_value())
                .maximum_discount_amount(request.maximum_discount_amount())
                .build();

        when(policyRepository.save(any(CouponPolicy.class))).thenReturn(savedPolicy);

        // when
        PolicyResponse response = policyService.createPolicy(request);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PolicyResponse.from(savedPolicy, "쿠폰 정책 생성 완료"), response);
    }

    @Test
    void createPolicy_alreadyExists_throwsException() {
        // given
        PolicyCreateRequest request = new PolicyCreateRequest(
                "DUPLICATE_COUPON",
                DiscountType.FIXED,
                5000,
                3000,
                0,
                0
        );

        when(policyRepository.existsByName("DUPLICATE_COUPON")).thenReturn(true);

        // when & then
        Assertions.assertThrows(PolicyAlreadyExistException.class, () -> {
            policyService.createPolicy(request);
        });
    }

    @Test
    void updatePolicy_success() {
        // given
        PolicyUpdateRequest request = new PolicyUpdateRequest(
                "WELCOME10",
                DiscountType.RATE,
                10000,
                0,
                15,
                0
        );

        CouponPolicy existingPolicy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME10")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .build();

        when(policyRepository.findByName("WELCOME10")).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(CouponPolicy.class))).thenReturn(existingPolicy);

        // when
        PolicyResponse response = policyService.updatePolicy(request);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PolicyResponse.from(existingPolicy, "쿠폰 정책이 수정되었습니다."), response);
    }

    @Test
    void updatePolicy_notFound_throwsException() {
        // given
        PolicyUpdateRequest request = new PolicyUpdateRequest(
                "NOT_EXIST",
                DiscountType.FIXED,
                5000,
                1000,
                0,
                1000
        );

        when(policyRepository.findByName("NOT_EXIST")).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(PolicyNotFoundException.class, () -> {
            policyService.updatePolicy(request);
        });
    }

    @Test
    void deletePolicyById_success() {
        // given
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("DELETE_ME")
                .discountType(DiscountType.RATE)
                .discount_value(0)
                .discount_percentage(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .build();

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // when
        PolicyResponse response = policyService.deletePolicyById(1L);

        // then
        verify(policyRepository).delete(policy);
        Assertions.assertEquals(PolicyResponse.from(policy, "쿠폰 정책이 삭제되었습니다."), response);
    }

    @Test
    void deletePolicyById_notFound_throwsException() {
        // given
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(PolicyNotFoundException.class, () -> {
            policyService.deletePolicyById(999L);
        });
    }

    @Test
    void deletePolicyByName_success() {
        // given
        CouponPolicy policy = CouponPolicy.builder()
                .id(2L)
                .name("DELETE_BY_NAME")
                .discountType(DiscountType.FIXED)
                .discount_value(2000)
                .minimum_order_amount(10000)
                .maximum_discount_amount(3000)
                .build();

        when(policyRepository.findByName("DELETE_BY_NAME")).thenReturn(Optional.of(policy));

        // when
        PolicyResponse response = policyService.deletePolicyByName("DELETE_BY_NAME");

        // then
        verify(policyRepository).delete(policy);
        Assertions.assertEquals(PolicyResponse.from(policy, "쿠폰 정책이 삭제되었습니다."), response);
    }

    @Test
    void deletePolicyByName_notFound_throwsException() {
        // given
        when(policyRepository.findByName("NOT_FOUND")).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(PolicyNotFoundException.class, () -> {
            policyService.deletePolicyByName("NOT_FOUND");
        });
    }
}


