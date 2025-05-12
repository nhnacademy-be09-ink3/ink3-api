package shop.ink3.api.user.membership.controller;

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
import shop.ink3.api.user.membership.dto.MembershipCreateRequest;
import shop.ink3.api.user.membership.dto.MembershipResponse;
import shop.ink3.api.user.membership.dto.MembershipUpdateRequest;
import shop.ink3.api.user.membership.service.MembershipService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/memberships")
public class MembershipController {
    private final MembershipService membershipService;

    @GetMapping("/{membershipId}")
    public ResponseEntity<CommonResponse<MembershipResponse>> getMembership(@PathVariable long membershipId) {
        return ResponseEntity.ok(CommonResponse.success(membershipService.getMembership(membershipId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<MembershipResponse>>> getMemberships(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(membershipService.getMemberships(pageable)));
    }

    @GetMapping("/default")
    public ResponseEntity<CommonResponse<MembershipResponse>> getDefaultMembership() {
        return ResponseEntity.ok(CommonResponse.success(membershipService.getDefaultMembership()));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<MembershipResponse>> createMembership(
            @RequestBody MembershipCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(membershipService.createMembership(request)));
    }

    @PutMapping("/{membershipId}")
    public ResponseEntity<CommonResponse<MembershipResponse>> updateMembership(
            @PathVariable long membershipId,
            @RequestBody MembershipUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.update(membershipService.updateMembership(membershipId, request)));
    }

    @PatchMapping("/{membershipId}/activate")
    public ResponseEntity<Void> activateMembership(@PathVariable long membershipId) {
        membershipService.activateMembership(membershipId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{membershipId}/deactivate")
    public ResponseEntity<Void> deactivateMembership(@PathVariable long membershipId) {
        membershipService.deactivateMembership(membershipId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{membershipId}/default")
    public ResponseEntity<Void> setDefaultMembership(@PathVariable long membershipId) {
        membershipService.setDefaultMembership(membershipId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{membershipId}")
    public ResponseEntity<Void> deleteMembership(@PathVariable long membershipId) {
        membershipService.deleteMembership(membershipId);
        return ResponseEntity.noContent().build();
    }
}
