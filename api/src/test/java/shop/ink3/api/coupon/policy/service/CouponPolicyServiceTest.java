package shop.ink3.api.coupon.policy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;

@ExtendWith(MockitoExtension.class)
public class CouponPolicyServiceTest {

    @Mock
    PolicyRepository policyRepository;

    @InjectMocks
    PolicyService policyService;

    @Test
    void createFixedPolicy() {
        CouponPolicy couponPolicy = CouponPolicy.builder()
                .id(1L)
                .name("test")
                .minimumOrderAmount(1000)
                .discountType(DiscountType.FIXED)
                .discountValue(5000)
                .createdAt(LocalDateTime.now())
                .build();
        when(policyRepository.save(any())).thenReturn(couponPolicy);
        PolicyCreateRequest request = new PolicyCreateRequest(
                "test",
                1000,
                DiscountType.FIXED,
                5000,
                0,
                0
        );
        PolicyResponse policyResponse = policyService.createPolicy(request);
        Assertions.assertNotNull(policyResponse);
        assertEquals(PolicyResponse.from(couponPolicy,"쿠폰 정책 생성 완료"), policyResponse);
    }

    @Test
    void createRatePolicy() {
        CouponPolicy couponPolicy = CouponPolicy.builder()
                .id(1L)
                .name("test")
                .minimumOrderAmount(1000)
                .discountType(DiscountType.RATE)
                .discountPercentage(20)
                .maximumDiscountAmount(10000)
                .createdAt(LocalDateTime.now())
                .build();
        when(policyRepository.save(any())).thenReturn(couponPolicy);
        PolicyCreateRequest request = new PolicyCreateRequest(
                "test",
                1000,
                DiscountType.RATE,
                0,
                20,
                10000
        );
        PolicyResponse policyResponse = policyService.createPolicy(request);
        Assertions.assertNotNull(policyResponse);
        assertEquals(PolicyResponse.from(couponPolicy,"쿠폰 정책 생성 완료"), policyResponse);
    }

    @Test
    void getPolicy() {
        // given
        CouponPolicy p1 = CouponPolicy.builder()
                .id(1L).name("A").createdAt(LocalDateTime.now()).build();
        CouponPolicy p2 = CouponPolicy.builder()
                .id(2L).name("B").createdAt(LocalDateTime.now()).build();

        when(policyRepository.findAll()).thenReturn(List.of(p1, p2));

        // when
        List<PolicyResponse> result = policyService.getPolicy();

        // then
        List<PolicyResponse> expected = List.of(p1, p2).stream()
                .map(p -> PolicyResponse.from(p, "정책 조회 성공"))
                .collect(Collectors.toList());

        assertEquals(expected.size(), result.size());
        assertIterableEquals(expected, result);
    }

    @Test
    void getPolicyById_success() {
        CouponPolicy p = CouponPolicy.builder()
                .id(99L).name("X").createdAt(LocalDateTime.now()).build();

        when(policyRepository.findById(99L)).thenReturn(Optional.of(p));

        PolicyResponse resp = policyService.getPolicyById(99L);

        assertEquals(
                PolicyResponse.from(p, "쿠폰 정책 조회 완료"),
                resp
        );
    }

    @Test
    void getPolicyById_notFound() {
        when(policyRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(
                PolicyNotFoundException.class,
                () -> policyService.getPolicyById(100L)
        );
    }

    @Test
    void updatePolicy_success() {
        CouponPolicy existing = CouponPolicy.builder()
                .id(10L)
                .name("OLD")
                .minimumOrderAmount(1000)
                .discountType(DiscountType.FIXED)
                .discountValue(100)
                .createdAt(LocalDateTime.now())
                .build();

        PolicyUpdateRequest req = new PolicyUpdateRequest(
                "NEW",
                DiscountType.RATE,
                2000,
                0,
                15,
                500
        );

        when(policyRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(policyRepository.save(existing)).thenReturn(existing);

        PolicyResponse resp = policyService.updatePolicy(10L, req);

        assertEquals("NEW", existing.getName());
        assertEquals(DiscountType.RATE, existing.getDiscountType());
        assertEquals(2000, existing.getMinimumOrderAmount());
        assertEquals(15, existing.getDiscountPercentage());
        assertEquals(500, existing.getMaximumDiscountAmount());

        assertEquals(
                PolicyResponse.from(existing, "쿠폰 정책이 수정되었습니다."),
                resp
        );

        verify(policyRepository).findById(10L);
        verify(policyRepository).save(existing);
    }

    @Test
    void updatePolicy_notFound() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class,
                () -> policyService.updatePolicy(99L,
                        new PolicyUpdateRequest("X", DiscountType.FIXED, 0, 0, 0, 0)
                )
        );

        verify(policyRepository).findById(99L);
        verify(policyRepository, never()).save(any());
    }

    @Test
    void deletePolicyById_success() {
        // given
        CouponPolicy existing = CouponPolicy.builder()
                .id(5L)
                .name("DEL")
                .createdAt(LocalDateTime.now())
                .build();

        when(policyRepository.findById(5L)).thenReturn(Optional.of(existing));

        // when
        PolicyResponse resp = policyService.deletePolicyById(5L);

        // then
        assertEquals(
                PolicyResponse.from(existing, "쿠폰 정책이 삭제되었습니다."),
                resp
        );

        verify(policyRepository).findById(5L);
        verify(policyRepository).delete(existing);
    }

    @Test
    void deletePolicyById_notFound() {
        when(policyRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class,
                () -> policyService.deletePolicyById(123L)
        );

        verify(policyRepository).findById(123L);
        verify(policyRepository, never()).delete(any());
    }

}
