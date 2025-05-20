package shop.ink3.api.order.refundPolicy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyCreateRequest;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyResponse;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyUpdateRequest;
import shop.ink3.api.order.refundPolicy.service.RefundPolicyService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/refundPolicies")
public class RefundPolicyController {
    private final RefundPolicyService refundPolicyService;

    @GetMapping("/{refundPolicyId}")
    public ResponseEntity<CommonResponse<RefundPolicyResponse>> getRefundPolicy(
            @PathVariable long refundPolicyId) {
        return ResponseEntity
                .ok(CommonResponse.success(refundPolicyService.getRefundPolicy(refundPolicyId)));
    }

    @GetMapping("/activate")
    public ResponseEntity<CommonResponse<RefundPolicyResponse>> getActivateRefundPolicy() {
        return ResponseEntity
                .ok(CommonResponse.success(refundPolicyService.getAvailableRefundPolicy()));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<RefundPolicyResponse>>> getRefundPolicyList(Pageable pageable) {
        return ResponseEntity
                .ok(CommonResponse.success(refundPolicyService.getRefundPolicyList(pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<RefundPolicyResponse>> createRefundPolicy(
            @RequestBody RefundPolicyCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(refundPolicyService.createRefundPolicy(request)));
    }

    @PutMapping("/{refundPolicyId}")
    public ResponseEntity<CommonResponse<RefundPolicyResponse>> updateRefundPolicy(
            @PathVariable long refundPolicyId,
            @RequestBody RefundPolicyUpdateRequest request) {
        return ResponseEntity
                .ok(CommonResponse.update(refundPolicyService.updateRefundPolicy(refundPolicyId, request)));
    }

    @PatchMapping("/activate/{refundPolicyId}")
    public ResponseEntity<Void> activateRefundPolicy(@PathVariable long refundPolicyId) {
        refundPolicyService.activate(refundPolicyId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deactivate/{refundPolicyId}")
    public ResponseEntity<Void> deactivateRefundPolicy(@PathVariable long refundPolicyId) {
        refundPolicyService.deactivate(refundPolicyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{refundPolicyId}")
    public ResponseEntity<Void> deleteRefundPolicy(@PathVariable long refundPolicyId) {
        refundPolicyService.deleteRefundPolicy(refundPolicyId);
        return ResponseEntity.ok().build();
    }

}
