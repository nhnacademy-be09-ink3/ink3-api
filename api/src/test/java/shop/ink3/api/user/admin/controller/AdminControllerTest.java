package shop.ink3.api.user.admin.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.user.admin.dto.AdminAuthResponse;
import shop.ink3.api.user.admin.dto.AdminCreatedRequest;
import shop.ink3.api.user.admin.dto.AdminPasswordUpdateRequest;
import shop.ink3.api.user.admin.dto.AdminResponse;
import shop.ink3.api.user.admin.dto.AdminUpdateRequest;
import shop.ink3.api.user.admin.entity.Admin;
import shop.ink3.api.user.admin.entity.AdminStatus;
import shop.ink3.api.user.admin.exception.AdminAuthNotFoundException;
import shop.ink3.api.user.admin.exception.AdminNotFoundException;
import shop.ink3.api.user.admin.service.AdminService;
import shop.ink3.api.user.common.exception.InvalidPasswordException;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AdminService adminService;

    @Test
    void checkAdminIdentifierAvailability() throws Exception {
        when(adminService.isLoginIdAvailable("test")).thenReturn(true);
        mockMvc.perform(get("/admins/check")
                        .queryParam("loginId", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.loginIdAvailable").value(true))
                .andDo(print());
    }

    @Test
    void checkUserIdentifierAvailabilityWithInvalidInput() throws Exception {
        when(adminService.isLoginIdAvailable("test")).thenReturn(false);
        mockMvc.perform(get("/admins/check")
                        .queryParam("loginId", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.loginIdAvailable").value(false))
                .andDo(print());
    }

    @Test
    void getAdmin() throws Exception {
        Admin admin = Admin.builder()
                .id(1L)
                .loginId("test")
                .password("test")
                .name("test")
                .status(AdminStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        AdminResponse response = AdminResponse.from(admin);
        when(adminService.getAdmin(1L)).thenReturn(response);
        mockMvc.perform(get("/admins/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getAdminWithNotFound() throws Exception {
        when(adminService.getAdmin(1L)).thenThrow(new AdminNotFoundException(1L));
        mockMvc.perform(get("/admins/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void getAdminAuth() throws Exception {
        Admin admin = Admin.builder()
                .id(1L)
                .loginId("test")
                .password("test")
                .name("test")
                .status(AdminStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        AdminAuthResponse response = AdminAuthResponse.from(admin);
        when(adminService.getAdminAuth("test")).thenReturn(response);
        mockMvc.perform(get("/admins/test/auth"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getAdminAuthWithNotFound() throws Exception {
        when(adminService.getAdminAuth("test")).thenThrow(new AdminAuthNotFoundException("test"));
        mockMvc.perform(get("/admins/test/auth"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void createAdmin() throws Exception {
        AdminCreatedRequest request = new AdminCreatedRequest("test", "test", "test");
        AdminResponse response = new AdminResponse(
                1L,
                "test",
                "test",
                AdminStatus.ACTIVE,
                null,
                LocalDateTime.now()
        );
        when(adminService.createAdmin(request)).thenReturn(response);
        mockMvc.perform(post("/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void updateAdmin() throws Exception {
        AdminUpdateRequest request = new AdminUpdateRequest("new");
        AdminResponse response = new AdminResponse(
                1L,
                "test",
                "new",
                AdminStatus.ACTIVE,
                null,
                LocalDateTime.now()
        );
        when(adminService.updateAdmin(1L, request)).thenReturn(response);
        mockMvc.perform(put("/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void updateAdminWithNotFound() throws Exception {
        AdminUpdateRequest request = new AdminUpdateRequest("new");
        when(adminService.updateAdmin(1L, request)).thenThrow(new AdminNotFoundException(1L));
        mockMvc.perform(put("/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void updateAdminPassword() throws Exception {
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("test", "test");
        doNothing().when(adminService).updateAdminPassword(1L, request);
        mockMvc.perform(patch("/admins/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateAdminPasswordWithInvalidPassword() throws Exception {
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("invalid", "new");
        doThrow(new InvalidPasswordException()).when(adminService).updateAdminPassword(1L, request);
        mockMvc.perform(patch("/admins/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void updateAdminPasswordWithNotFound() throws Exception {
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("test", "test");
        doThrow(new AdminNotFoundException(1L)).when(adminService).updateAdminPassword(1L, request);
        mockMvc.perform(patch("/admins/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void activateAdmin() throws Exception {
        doNothing().when(adminService).activateAdmin(1L);
        mockMvc.perform(patch("/admins/1/activate"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void activateAdminWithNotFound() throws Exception {
        doThrow(new AdminNotFoundException(1L)).when(adminService).activateAdmin(1L);
        mockMvc.perform(patch("/admins/1/activate"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void dormantAdmin() throws Exception {
        doNothing().when(adminService).markAsDormantAdmin(1L);
        mockMvc.perform(patch("/admins/1/dormant"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void dormantAdminWithNotFound() throws Exception {
        doThrow(new AdminNotFoundException(1L)).when(adminService).markAsDormantAdmin(1L);
        mockMvc.perform(patch("/admins/1/dormant"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void withdrawAdmin() throws Exception {
        doNothing().when(adminService).withdrawAdmin(1L);
        mockMvc.perform(patch("/admins/1/withdraw"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void withdrawAdminWithNotFound() throws Exception {
        doThrow(new AdminNotFoundException(1L)).when(adminService).withdrawAdmin(1L);
        mockMvc.perform(patch("/admins/1/withdraw"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void updateLastLogin() throws Exception {
        doNothing().when(adminService).updateLastLogin(1L);
        mockMvc.perform(patch("/admins/1/last-login"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateLastLoginWithNotFound() throws Exception {
        doThrow(new AdminNotFoundException(1L)).when(adminService).updateLastLogin(1L);
        mockMvc.perform(patch("/admins/1/last-login"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void deleteAdmin() throws Exception {
        doNothing().when(adminService).deleteAdmin(1L);
        mockMvc.perform(delete("/admins/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deleteAdminWithNotFound() throws Exception {
        doThrow(new AdminNotFoundException(1L)).when(adminService).deleteAdmin(1L);
        mockMvc.perform(delete("/admins/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
