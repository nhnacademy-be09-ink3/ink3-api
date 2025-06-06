package shop.ink3.api.user.point.policy.controller;

import jakarta.validation.Valid;
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
import shop.ink3.api.user.point.policy.dto.PointPolicyCreateRequest;
import shop.ink3.api.user.point.policy.dto.PointPolicyResponse;
import shop.ink3.api.user.point.policy.dto.PointPolicyStatisticsResponse;
import shop.ink3.api.user.point.policy.dto.PointPolicyUpdateRequest;
import shop.ink3.api.user.point.policy.service.PointPolicyService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point-policies")
public class PointPolicyController {
    private final PointPolicyService pointPolicyService;

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PointPolicyResponse>>> getPointPolicies(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(pointPolicyService.getPointPolicies(pageable)));
    }

    @GetMapping("/statistics")
    public ResponseEntity<CommonResponse<PointPolicyStatisticsResponse>> getPointPolicyStatistics() {
        return ResponseEntity.ok(CommonResponse.success(pointPolicyService.getPointPolicyStatistics()));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<PointPolicyResponse>> createPointPolicy(
            @RequestBody @Valid PointPolicyCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.create(pointPolicyService.createPointPolicy(request)));
    }

    @PutMapping("/{pointPolicyId}")
    public ResponseEntity<CommonResponse<PointPolicyResponse>> updatePointPolicy(
            @PathVariable long pointPolicyId, @RequestBody @Valid PointPolicyUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.update(pointPolicyService.updatePointPolicy(pointPolicyId, request)));
    }

    @PatchMapping("/{pointPolicyId}")
    public ResponseEntity<Void> activatePointPolicy(@PathVariable long pointPolicyId) {
        pointPolicyService.activatePointPolicy(pointPolicyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{pointPolicyId}")
    public ResponseEntity<Void> deletePointPolicy(@PathVariable long pointPolicyId) {
        pointPolicyService.deletePointPolicy(pointPolicyId);
        return ResponseEntity.noContent().build();
    }
}
