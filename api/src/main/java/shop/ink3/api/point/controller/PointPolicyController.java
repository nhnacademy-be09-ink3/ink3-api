package shop.ink3.api.point.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.ink3.api.point.PointPolicy;
import shop.ink3.api.point.dto.PointPolicyDto;
import shop.ink3.api.point.service.PointPolicyService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/point-policies")
@RequiredArgsConstructor
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @PostMapping
    public ResponseEntity<PointPolicyDto.Response> createPointPolicy(@Valid @RequestBody PointPolicyDto.Request request) {
        PointPolicy createdPolicy = pointPolicyService.createPointPolicy(
                request.getName(),
                request.getEarnPoint(),
                request.getIsAvailable() != null ? request.getIsAvailable() : true

        );
        return new ResponseEntity<>(PointPolicyDto.Response.from(createdPolicy), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointPolicyDto.Response> getPointPolicyById(@PathVariable Long id) {
        PointPolicy pointPolicy = pointPolicyService.getPointPolicyById(id);
        return ResponseEntity.ok(PointPolicyDto.Response.from(pointPolicy));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PointPolicyDto.Response> getPointPolicyByName(@PathVariable String name) {
        PointPolicy pointPolicy = pointPolicyService.getPointPolicyByName(name);
        return ResponseEntity.ok(PointPolicyDto.Response.from(pointPolicy));
    }

    @GetMapping("/active")
    public ResponseEntity<PointPolicyDto.Response> getActivePointPolicy() {
        PointPolicy pointPolicy = pointPolicyService.getActivePointPolicy();
        return ResponseEntity.ok(PointPolicyDto.Response.from(pointPolicy));
    }

    @GetMapping
    public ResponseEntity<List<PointPolicyDto.Response>> getAllPointPolicies() {
        List<PointPolicy> policies = pointPolicyService.getAllPointPolicies();
        List<PointPolicyDto.Response> responseList = policies.stream()
                .map(PointPolicyDto.Response::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointPolicyDto.Response> updatePointPolicy(
            @PathVariable Long id,
            @Valid @RequestBody PointPolicyDto.UpdateRequest request) {
        PointPolicy updatedPolicy = pointPolicyService.updatePointPolicy(
                id,
                request.getName(),
                request.getEarnPoint(),
                request.getIsAvailable()

        );
        return ResponseEntity.ok(PointPolicyDto.Response.from(updatedPolicy));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<PointPolicyDto.Response> activatePointPolicy(@PathVariable Long id) {
        PointPolicy activatedPolicy = pointPolicyService.activatePointPolicy(id);
        return ResponseEntity.ok(PointPolicyDto.Response.from(activatedPolicy));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PointPolicyDto.Response> deactivatePointPolicy(@PathVariable Long id) {
        PointPolicy deactivatedPolicy = pointPolicyService.deactivatePointPolicy(id);
        return ResponseEntity.ok(PointPolicyDto.Response.from(deactivatedPolicy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePointPolicy(@PathVariable Long id) {
        pointPolicyService.deletePointPolicy(id);
        return ResponseEntity.noContent().build();
    }
}
