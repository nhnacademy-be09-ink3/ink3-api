package shop.ink3.api.user.admin.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import shop.ink3.api.user.common.exception.InvalidPasswordException;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    AdminRepository adminRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    AdminService adminService;

    @Test
    void isLoginIdAvailable() {
        when(adminRepository.existsByLoginId(anyString())).thenReturn(false);
        Assertions.assertTrue(adminService.isLoginIdAvailable("loginId"));
    }

    @Test
    void isLoginIdAvailableWithExists() {
        when(adminRepository.existsByLoginId(anyString())).thenReturn(true);
        Assertions.assertFalse(adminService.isLoginIdAvailable("loginId"));
    }

    @Test
    void getAdmin() {
        Admin admin = Admin.builder()
                .id(1L)
                .loginId("test")
                .password("test")
                .name("test")
                .status(AdminStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        AdminResponse response = adminService.getAdmin(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(AdminResponse.from(admin), response);
    }

    @Test
    void getAdminNotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFoundException.class, () -> adminService.getAdmin(1L));
    }

    @Test
    void getAdminAuth() {
        Admin admin = Admin.builder()
                .id(1L)
                .loginId("test")
                .password("test")
                .name("test")
                .status(AdminStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        when(adminRepository.findByLoginId("test")).thenReturn(Optional.of(admin));
        AdminAuthResponse response = adminService.getAdminAuth("test");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(AdminAuthResponse.from(admin), response);
    }

    @Test
    void getAdminAuthNotFound() {
        when(adminRepository.findByLoginId("test")).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminAuthNotFoundException.class, () -> adminService.getAdminAuth("test"));
    }

    @Test
    void createAdmin() {
        AdminCreatedRequest request = new AdminCreatedRequest("test", "test", "test");
        when(adminRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AdminResponse response = adminService.createAdmin(request);
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals("test", response.loginId()),
                () -> Assertions.assertEquals("test", response.name())
        );
    }

    @Test
    void updateAdmin() {
        Admin admin = Admin.builder()
                .id(1L)
                .loginId("test")
                .password("test")
                .name("test")
                .status(AdminStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        AdminUpdateRequest request = new AdminUpdateRequest("new");
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(adminRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AdminResponse response = adminService.updateAdmin(1L, request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("new", response.name());
    }

    @Test
    void updateAdminWithNotFound() {
        AdminUpdateRequest request = new AdminUpdateRequest("new");
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFoundException.class, () -> adminService.updateAdmin(1L, request));
    }

    @Test
    void updateAdminPassword() {
        Admin admin = Admin.builder().id(1L).password(passwordEncoder.encode("old")).build();
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("old", "new");
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        adminService.updateAdminPassword(1L, request);
        Assertions.assertTrue(passwordEncoder.matches(request.newPassword(), admin.getPassword()));
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void updateAdminPasswordWithInvalidPassword() {
        Admin admin = Admin.builder().id(1L).password(passwordEncoder.encode("old")).build();
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("invalid", "new");
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        Assertions.assertThrows(InvalidPasswordException.class, () -> adminService.updateAdminPassword(1L, request));
    }

    @Test
    void updateUserPasswordWithNotFound() {
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("old", "new");
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFoundException.class, () -> adminService.updateAdminPassword(1L, request));
    }

    @Test
    void activateAdmin() {
        Admin admin = Admin.builder().id(1L).status(AdminStatus.DORMANT).build();
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        adminService.activateAdmin(1L);
        Assertions.assertEquals(AdminStatus.ACTIVE, admin.getStatus());
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void activateAdminWithNotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFoundException.class, () -> adminService.activateAdmin(1L));
    }

    @Test
    void markAsDormantAdmin() {
        Admin admin = Admin.builder().id(1L).status(AdminStatus.ACTIVE).build();
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        adminService.markAsDormantAdmin(1L);
        Assertions.assertEquals(AdminStatus.DORMANT, admin.getStatus());
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void markAsDormantAdminWithNotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFoundException.class, () -> adminService.markAsDormantAdmin(1L));
    }

    @Test
    void withdrawAdmin() {
        Admin admin = Admin.builder().id(1L).status(AdminStatus.ACTIVE).build();
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        adminService.withdrawAdmin(1L);
        Assertions.assertEquals(AdminStatus.WITHDRAWN, admin.getStatus());
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void withdrawAdminWithNotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFoundException.class, () -> adminService.withdrawAdmin(1L));
    }

    @Test
    void deleteAdmin() {
        Admin admin = Admin.builder().id(1L).build();
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        adminService.deleteAdmin(1L);
        verify(adminRepository, times(1)).delete(admin);
    }

    @Test
    void deleteAdminWithNotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFoundException.class, () -> adminService.deleteAdmin(1L));
    }
}
