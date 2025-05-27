package shop.ink3.api.order.shippingPolicy.controller;

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
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyCreateRequest;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyResponse;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyUpdateRequest;
import shop.ink3.api.order.shippingPolicy.service.ShippingPolicyService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shipping-policies")
public class ShippingPolicyController {

    private final ShippingPolicyService shippingPolicyService;

    @GetMapping("/{shippingPolicyId}")
    public ResponseEntity<CommonResponse<ShippingPolicyResponse>> getShippingPolicy(
            @PathVariable long shippingPolicyId) {
        return ResponseEntity
                .ok(CommonResponse.success(shippingPolicyService.getShippingPolicy(shippingPolicyId)));
    }

    @GetMapping("/activate")
    public ResponseEntity<CommonResponse<ShippingPolicyResponse>> getActivateShippingPolicy() {
        return ResponseEntity
                .ok(CommonResponse.success(shippingPolicyService.getActivateShippingPolicy()));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<ShippingPolicyResponse>>> getShippingPolicies(Pageable pageable) {
        return ResponseEntity
                .ok(CommonResponse.success(shippingPolicyService.getShippingPolicyList(pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<ShippingPolicyResponse>> createShippingPolicy(
            @RequestBody ShippingPolicyCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(shippingPolicyService.createShippingPolicy(request)));
    }

    @PutMapping("/{shippingPolicyId}")
    public ResponseEntity<CommonResponse<ShippingPolicyResponse>> updateShippingPolicy(
            @PathVariable long shippingPolicyId,
            @RequestBody ShippingPolicyUpdateRequest request) {
        return ResponseEntity
                .ok(CommonResponse.update(shippingPolicyService.updateShippingPolicy(shippingPolicyId, request)));
    }

    @PatchMapping("/{shippingPolicyId}/activate")
    public ResponseEntity<Void> activateShippingPolicy(@PathVariable long shippingPolicyId) {
        shippingPolicyService.activate(shippingPolicyId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{shippingPolicyId}/deactivate")
    public ResponseEntity<Void> deactivateShippingPolicy(@PathVariable long shippingPolicyId) {
        shippingPolicyService.deactivate(shippingPolicyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{shippingPolicyId}")
    public ResponseEntity<Void> deleteShippingPolicy(@PathVariable long shippingPolicyId) {
        shippingPolicyService.deleteShippingPolicy(shippingPolicyId);
        return ResponseEntity.ok().build();
    }
}
