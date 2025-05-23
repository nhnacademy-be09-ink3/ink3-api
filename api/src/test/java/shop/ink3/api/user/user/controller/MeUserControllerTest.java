package shop.ink3.api.user.user.controller;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.user.common.exception.InvalidPasswordException;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.user.dto.UserDetailResponse;
import shop.ink3.api.user.user.dto.UserPasswordUpdateRequest;
import shop.ink3.api.user.user.dto.UserResponse;
import shop.ink3.api.user.user.dto.UserUpdateRequest;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.service.UserService;

@WebMvcTest(MeUserController.class)
class MeUserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @Test
    void getCurrentUser() throws Exception {
        User user = User.builder()
                .id(1L)
                .loginId("test")
                .name("test")
                .email("test@test.com")
                .phone("010-1234-5678")
                .birthday(LocalDate.of(2025, 1, 1))
                .point(1000)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        UserResponse response = UserResponse.from(user);
        when(userService.getUser(1L)).thenReturn(response);
        mockMvc.perform(get("/users/me").header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getCurrentUserWithNotFound() throws Exception {
        when(userService.getUser(1L)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(get("/users/me").header("X-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void getCurrentUserDetail() throws Exception {
        User user = User.builder()
                .id(1L)
                .loginId("test")
                .name("test")
                .email("test@test.com")
                .phone("010-1234-5678")
                .birthday(LocalDate.of(2025, 1, 1))
                .point(1000)
                .membership(Membership.builder().id(1L).build())
                .status(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        UserDetailResponse response = UserDetailResponse.from(user);
        when(userService.getUserDetail(1L)).thenReturn(response);
        mockMvc.perform(get("/users/me/detail").header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.membership.id").value(1L))
                .andDo(print());
    }

    @Test
    void getCurrentUserDetailWithNotFound() throws Exception {
        when(userService.getUserDetail(1L)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(get("/users/me/detail").header("X-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void updateCurrentUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest(
                "new",
                "new@new.com",
                "010-5150-5150",
                LocalDate.of(2025, 1, 2)
        );
        UserResponse response = new UserResponse(
                1L,
                "new",
                "new",
                "new@new.com",
                "010-5150-5150",
                LocalDate.of(2025, 1, 2),
                0,
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(userService.updateUser(1L, request)).thenReturn(response);
        mockMvc.perform(put("/users/me")
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
    void updateCurrentUserWithNotFound() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest(
                "new",
                "new@new.com",
                "010-5150-5150",
                LocalDate.of(2025, 1, 2)
        );
        when(userService.updateUser(1L, request)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(put("/users/me")
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
    void updateCurrentUserPassword() throws Exception {
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("old", "new");
        doNothing().when(userService).updateUserPassword(1L, request);
        mockMvc.perform(patch("/users/me/password")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateCurrentUserPasswordWithNotFound() throws Exception {
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("old", "new");
        doThrow(new UserNotFoundException(1L)).when(userService).updateUserPassword(1L, request);
        mockMvc.perform(patch("/users/me/password")
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
    void updateCurrentUserPasswordWithInvalidPassword() throws Exception {
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("invalid", "new");
        doThrow(new InvalidPasswordException()).when(userService).updateUserPassword(1L, request);
        mockMvc.perform(patch("/users/me/password")
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
    void withdrawCurrentUser() throws Exception {
        doNothing().when(userService).withdrawUser(1L);
        mockMvc.perform(patch("/users/me/withdraw").header("X-User-Id", 1))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void withdrawCurrentUserWithNotFound() throws Exception {
        doThrow(new UserNotFoundException(1L)).when(userService).withdrawUser(1L);
        mockMvc.perform(patch("/users/me/withdraw").header("X-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
