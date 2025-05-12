package shop.ink3.api.user.admin.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.user.admin.dto.AdminAuthResponse;
import shop.ink3.api.user.admin.dto.AdminCreatedRequest;
import shop.ink3.api.user.admin.dto.AdminPasswordUpdateRequest;
import shop.ink3.api.user.admin.dto.AdminResponse;
import shop.ink3.api.user.admin.dto.AdminUpdateRequest;
import shop.ink3.api.user.admin.entity.Admin;
import shop.ink3.api.user.admin.entity.AdminStatus;
import shop.ink3.api.user.admin.exception.AdminAuthNotFoundException;
import shop.ink3.api.user.admin.exception.AdminNotFoundException;
import shop.ink3.api.user.admin.repository.AdminRepository;
import shop.ink3.api.user.common.exception.DormantException;
import shop.ink3.api.user.common.exception.InvalidPasswordException;
import shop.ink3.api.user.common.exception.WithdrawnException;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !adminRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public AdminResponse getAdmin(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        return AdminResponse.from(admin);
    }

    @Transactional(readOnly = true)
    public AdminAuthResponse getAdminAuth(String loginId) {
        Admin admin = adminRepository.findByLoginId(loginId).orElseThrow(() -> new AdminAuthNotFoundException(loginId));
        if (admin.getStatus() == AdminStatus.DORMANT) {
            throw new DormantException(admin.getId());
        }
        if (admin.getStatus() == AdminStatus.WITHDRAWN) {
            throw new WithdrawnException(admin.getId());
        }
        return AdminAuthResponse.from(admin);
    }

    public AdminResponse createAdmin(AdminCreatedRequest request) {
        Admin admin = Admin.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .status(AdminStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        return AdminResponse.from(adminRepository.save(admin));
    }

    public AdminResponse updateAdmin(long adminId, AdminUpdateRequest request) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        admin.update(request.name());
        return AdminResponse.from(adminRepository.save(admin));
    }

    public void updateAdminPassword(long adminId, AdminPasswordUpdateRequest request) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        if (!passwordEncoder.matches(request.currentPassword(), admin.getPassword())) {
            throw new InvalidPasswordException();
        }
        admin.updatePassword(passwordEncoder.encode(request.newPassword()));
        adminRepository.save(admin);
    }

    public void activateAdmin(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        admin.activate();
        adminRepository.save(admin);
    }

    public void markAsDormantAdmin(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        admin.markAsDormant();
        adminRepository.save(admin);
    }

    public void withdrawAdmin(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        admin.withdraw();
        adminRepository.save(admin);
    }

    public void updateLastLogin(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        admin.updateLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);
    }

    public void deleteAdmin(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException(adminId));
        adminRepository.delete(admin);
    }
}
