package shop.ink3.api.user.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.user.admin.dto.AdminPasswordUpdateRequest;
import shop.ink3.api.user.admin.dto.AdminResponse;
import shop.ink3.api.user.admin.dto.AdminUpdateRequest;
import shop.ink3.api.user.admin.service.AdminService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admins")
public class MeAdminController {
    private final AdminService adminService;

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<AdminResponse>> getCurrentAdmin(@RequestHeader("X-User-Id") long adminId) {
        return ResponseEntity.ok(CommonResponse.success(adminService.getAdmin(adminId)));
    }

    @PutMapping("/me")
    public ResponseEntity<CommonResponse<AdminResponse>> updateCurrentAdmin(
            @RequestHeader("X-User-Id") long adminId,
            @RequestBody @Valid AdminUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.update(adminService.updateAdmin(adminId, request)));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updateCurrentAdminPassword(
            @RequestHeader("X-User-Id") long adminId,
            @RequestBody @Valid AdminPasswordUpdateRequest request
    ) {
        adminService.updateAdminPassword(adminId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/withdraw")
    public ResponseEntity<Void> withdrawCurrentAdmin(@RequestHeader("X-User-Id") long adminId) {
        adminService.withdrawAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}
