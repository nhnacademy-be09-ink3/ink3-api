package shop.ink3.api.coupon.policy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
import shop.ink3.api.coupon.policy.service.PolicyService;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    // 정책 전체 조회
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PolicyResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(policyService.getPolicy(pageable)));
    }

    // 정책 ID로 조회
    @GetMapping("/{policyId}")
    public ResponseEntity<CommonResponse<PolicyResponse>> getPolicyById(@PathVariable long policyId) {
        return ResponseEntity.ok(CommonResponse.success(policyService.getPolicyById(policyId)));
    }

    // 정책 생성
    @PostMapping
    public ResponseEntity<CommonResponse<PolicyResponse>> createPolicy(@Valid @RequestBody PolicyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.create(policyService.createPolicy(request)));
    }

    // 정책 수정 (ID 기반)
    @PutMapping("/{policyId}")
    public ResponseEntity<CommonResponse<PolicyResponse>> updatePolicy(
            @PathVariable Long policyId,
            @RequestBody PolicyUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(policyService.updatePolicy(policyId, request)));
    }

    // 정책 ID로 삭제
    @DeleteMapping("/{policyId}")
    public ResponseEntity<CommonResponse<PolicyResponse>> deletePolicyById(@PathVariable long policyId) {
        return ResponseEntity.ok(CommonResponse.success(policyService.deletePolicyById(policyId)));
    }
}


