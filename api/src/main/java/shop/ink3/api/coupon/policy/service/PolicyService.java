package shop.ink3.api.coupon.policy.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

    public List<PolicyResponse> getPolicy() {
        List<CouponPolicy> policies = policyRepository.findAll();
        return policies.stream()
                .map(policy -> PolicyResponse.from(policy))
                .collect(Collectors.toList());
    }

    public PolicyResponse getPolicyById(long policyId) {
        CouponPolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));

        return PolicyResponse.from(Objects.requireNonNull(policy));
    }


    @Transactional
    public PolicyResponse createPolicy(PolicyCreateRequest req) {
        // 중복 체크
        if (policyRepository.existsByName(req.name())) {
            throw new PolicyAlreadyExistException("이미 존재하는 쿠폰");
        }

        // DTO에서 받은 createdAt을 그대로 사용
        CouponPolicy policy = CouponPolicy.builder()
                .name(req.name())
                .minimumOrderAmount(req.minimumOrderAmount())
                .discountType(req.discountType())
                .discountValue(req.discountValue())
                .discountPercentage(req.discountPercentage()) // ← 빠졌던 부분
                .maximumDiscountAmount(req.maximumDiscountAmount())
                .createdAt(LocalDateTime.now())
                .build();


        CouponPolicy saved = policyRepository.save(policy);
        return PolicyResponse.from(saved);
    }



    @Transactional
    public PolicyResponse updatePolicy(Long id, PolicyUpdateRequest request) {
        CouponPolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));

        policy.update(
                request.name(),
                request.discountType(),
                request.minimumOrderAmount(),
                request.discountValue(),
                request.discountPercentage(),
                request.maximumDiscountAmount()
        );
        CouponPolicy saved = policyRepository.save(policy);
        return PolicyResponse.from(policy);
    }

    @Transactional
    public PolicyResponse deletePolicyById(long policyId) {
        CouponPolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("없는 쿠폰 정책"));
        policyRepository.delete(Objects.requireNonNull(policy));
        return PolicyResponse.from(policy);
    }

}
