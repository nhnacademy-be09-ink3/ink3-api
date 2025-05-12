package shop.ink3.api.coupon.policy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
import shop.ink3.api.coupon.policy.service.PolicyService;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    // 정책 ID로 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PolicyResponse>> getPolicyById(@PathVariable long id) {
        return ResponseEntity.ok(CommonResponse.success(policyService.getPolicyById(id)));
    }

    // 정책 이름으로 조회
    @GetMapping("/policyName/{name}")
    public ResponseEntity<CommonResponse<PolicyResponse>> getPolicyByName(@PathVariable String name) {
        return ResponseEntity.ok(CommonResponse.success(policyService.getPolicyByName(name)));
    }

    // 정책 생성
    @PostMapping
    public ResponseEntity<CommonResponse<PolicyResponse>> createPolicy(@RequestBody PolicyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.create(policyService.createPolicy(request)));
    }

    // 정책 수정
    @PutMapping
    public ResponseEntity<CommonResponse<PolicyResponse>> updatePolicy(@RequestBody PolicyUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(policyService.updatePolicy(request)));
    }

    // 정책 ID로 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<PolicyResponse>> deletePolicyById(@PathVariable long id) {
        return ResponseEntity.ok(CommonResponse.success(policyService.deletePolicyById(id)));
    }


    // 정책 이름으로 삭제
    @DeleteMapping("/policyName/{name}")
    public ResponseEntity<CommonResponse<PolicyResponse>> deletePolicyByName(@PathVariable String name) {
        return ResponseEntity.ok(CommonResponse.success(policyService.deletePolicyByName(name)));
    }
}

