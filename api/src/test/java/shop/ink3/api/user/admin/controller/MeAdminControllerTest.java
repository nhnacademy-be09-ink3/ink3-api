package shop.ink3.api.user.admin.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import shop.ink3.api.user.admin.dto.AdminPasswordUpdateRequest;
import shop.ink3.api.user.admin.dto.AdminResponse;
import shop.ink3.api.user.admin.dto.AdminUpdateRequest;
import shop.ink3.api.user.admin.entity.Admin;
import shop.ink3.api.user.admin.entity.AdminStatus;
import shop.ink3.api.user.admin.exception.AdminNotFoundException;
import shop.ink3.api.user.admin.service.AdminService;
import shop.ink3.api.user.common.exception.InvalidPasswordException;

@WebMvcTest(MeAdminController.class)
class MeAdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AdminService adminService;

    @Test
    void getCurrentAdmin() throws Exception {
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
        mockMvc.perform(get("/admins/me").header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getCurrentAdminNotFound() throws Exception {
        when(adminService.getAdmin(1L)).thenThrow(new AdminNotFoundException(1L));
        mockMvc.perform(get("/admins/me").header("X-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void updateCurrentAdmin() throws Exception {
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
        mockMvc.perform(put("/admins/me")
                        .header("X-User-Id", 1)
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
    void updateCurrentAdminNotFound() throws Exception {
        AdminUpdateRequest request = new AdminUpdateRequest("new");
        when(adminService.updateAdmin(1L, request)).thenThrow(new AdminNotFoundException(1L));
        mockMvc.perform(put("/admins/me")
                        .header("X-User-Id", 1)
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
    void updateCurrentAdminPassword() throws Exception {
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("test", "test");
        doNothing().when(adminService).updateAdminPassword(1L, request);
        mockMvc.perform(patch("/admins/me/password")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateAdminPasswordWithInvalidPassword() throws Exception {
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("invalid", "new");
        doThrow(new InvalidPasswordException()).when(adminService).updateAdminPassword(1L, request);
        mockMvc.perform(patch("/admins/me/password")
                        .header("X-User-Id", 1)
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
    void updateCurrentAdminPasswordNotFound() throws Exception {
        AdminPasswordUpdateRequest request = new AdminPasswordUpdateRequest("test", "test");
        doThrow(new AdminNotFoundException(1L)).when(adminService).updateAdminPassword(1L, request);
        mockMvc.perform(patch("/admins/me/password")
                        .header("X-User-Id", 1)
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
    void withdrawCurrentAdmin() throws Exception {
        doNothing().when(adminService).withdrawAdmin(1L);
        mockMvc.perform(patch("/admins/me/withdraw").header("X-User-Id", 1))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void withdrawCurrentAdminNotFound() throws Exception {
        doThrow(new AdminNotFoundException(1L)).when(adminService).withdrawAdmin(1L);
        mockMvc.perform(patch("/admins/me/withdraw").header("X-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
