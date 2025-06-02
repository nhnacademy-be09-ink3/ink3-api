package shop.ink3.api.user.admin.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.user.admin.dto.AdminAuthResponse;
import shop.ink3.api.user.admin.dto.AdminCreatedRequest;
import shop.ink3.api.user.admin.dto.AdminPasswordUpdateRequest;
import shop.ink3.api.user.admin.dto.AdminResponse;
import shop.ink3.api.user.admin.dto.AdminUpdateRequest;
import shop.ink3.api.user.admin.service.AdminService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/check")
    public ResponseEntity<CommonResponse<Map<String, Boolean>>> checkAdminIdentifierAvailability(
            @RequestParam String loginId
    ) {
        Map<String, Boolean> result = new HashMap<>();
        if (Objects.nonNull(loginId)) {
            result.put("loginIdAvailable", adminService.isLoginIdAvailable(loginId));
        }
        return ResponseEntity.ok(CommonResponse.success(result));
    }

    /**
     * Retrieves details of an admin by their unique ID.
     *
     * @param adminId the unique identifier of the admin
     * @return a response entity containing the admin details wrapped in a common response
     */
    @GetMapping("/{adminId}")
    public ResponseEntity<CommonResponse<AdminResponse>> getAdmin(@PathVariable long adminId) {
        return ResponseEntity.ok(CommonResponse.success(adminService.getAdmin(adminId)));
    }

    /**
     * Retrieves authentication information for the admin identified by the given login ID.
     *
     * @param loginId the login ID of the admin
     * @return a response containing the admin's authentication details
     */
    @GetMapping("/{loginId}/auth")
    public ResponseEntity<CommonResponse<AdminAuthResponse>> getAdminAuth(@PathVariable String loginId) {
        return ResponseEntity.ok(CommonResponse.success(adminService.getAdminAuth(loginId)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<AdminResponse>> createAdmin(@RequestBody @Valid AdminCreatedRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(adminService.createAdmin(request)));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<CommonResponse<AdminResponse>> updateAdmin(
            @PathVariable long adminId,
            @RequestBody @Valid AdminUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.update(adminService.updateAdmin(adminId, request)));
    }

    @PatchMapping("/{adminId}/password")
    public ResponseEntity<Void> updateAdminPassword(
            @PathVariable long adminId,
            @RequestBody @Valid AdminPasswordUpdateRequest request
    ) {
        adminService.updateAdminPassword(adminId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/activate")
    public ResponseEntity<Void> activateAdmin(@PathVariable long adminId) {
        adminService.activateAdmin(adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/dormant")
    public ResponseEntity<Void> dormantAdmin(@PathVariable long adminId) {
        adminService.markAsDormantAdmin(adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/withdraw")
    public ResponseEntity<Void> withdrawAdmin(@PathVariable long adminId) {
        adminService.withdrawAdmin(adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable long adminId) {
        adminService.updateLastLogin(adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable long adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}
