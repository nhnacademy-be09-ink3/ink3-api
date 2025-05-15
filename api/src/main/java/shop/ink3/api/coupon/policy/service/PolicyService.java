package shop.ink3.api.coupon.policy.service;

import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.exception.PolicyAlreadyExistException;
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;

@RequiredArgsConstructor
@Service
public class PolicyService {
    private final PolicyRepository policyRepository;

    public PolicyResponse getPolicyById(long policyId) {
        CouponPolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));

        return PolicyResponse.from(Objects.requireNonNull(policy),"쿠폰 정책 조회 완료");
    }

    public PolicyResponse getPolicyByName(String policyName) {
        CouponPolicy policy = policyRepository.findByName(policyName)
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));
        return PolicyResponse.from(Objects.requireNonNull(policy),"쿠폰 정책 조회 완료");
    }

    @Transactional
    public PolicyResponse createPolicy(PolicyCreateRequest policyCreateRequest) {
        boolean hasDefault = policyRepository.existsByName(policyCreateRequest.name());
        if (hasDefault) {
            throw new PolicyAlreadyExistException("이미 존재하는 쿠폰");
        }
        CouponPolicy policy = CouponPolicy.builder()
                .name(policyCreateRequest.name())
                .discountType(policyCreateRequest.discountType())
                .discount_value(policyCreateRequest.discount_value())
                .minimum_order_amount(policyCreateRequest.minimum_order_amount())
                .maximum_discount_amount(policyCreateRequest.maximum_discount_amount())
                .build();
        return PolicyResponse.from(policyRepository.save(policy),"쿠폰 정책 생성 완료");
    }

    @Transactional
    public PolicyResponse updatePolicy(PolicyUpdateRequest policyUpdateRequest) {
        CouponPolicy policy = policyRepository.findByName(policyUpdateRequest.name())
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));

        policy.update(policyUpdateRequest.name(), policyUpdateRequest.discountType(),
                policyUpdateRequest.minimum_order_amount(),
                policyUpdateRequest.discount_value(),
                policyUpdateRequest.maximum_discount_amount());
        return PolicyResponse.from(policyRepository.save(policy),"쿠폰 정책이 수정되었습니다.");
    }

    @Transactional
    public PolicyResponse deletePolicyById(long policyId) {
        CouponPolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));
        policyRepository.delete(Objects.requireNonNull(policy));
        return PolicyResponse.from(policy,"쿠폰 정책이 삭제되었습니다.");
    }

    @Transactional
    public PolicyResponse deletePolicyByName(String policyName) {
        CouponPolicy policy = policyRepository.findByName(policyName)
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));
        policyRepository.delete(Objects.requireNonNull(policy));
        return PolicyResponse.from(policy, "쿠폰 정책이 삭제되었습니다.");
    }
}
