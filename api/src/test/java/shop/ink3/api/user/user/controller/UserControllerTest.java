package shop.ink3.api.user.user.controller;

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
import shop.ink3.api.user.membership.exception.MembershipNotFoundException;
import shop.ink3.api.user.user.dto.UserAuthResponse;
import shop.ink3.api.user.user.dto.UserCreateRequest;
import shop.ink3.api.user.user.dto.UserDetailResponse;
import shop.ink3.api.user.user.dto.UserMembershipUpdateRequest;
import shop.ink3.api.user.user.dto.UserPasswordUpdateRequest;
import shop.ink3.api.user.user.dto.UserResponse;
import shop.ink3.api.user.user.dto.UserUpdateRequest;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;
import shop.ink3.api.user.user.exception.UserAuthNotFoundException;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @Test
    void getUser() throws Exception {
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
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getUserWithNotFound() throws Exception {
        when(userService.getUser(1L)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void getUserDetail() throws Exception {
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
        mockMvc.perform(get("/users/1/detail"))
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
    void getUserDetailWithNotFound() throws Exception {
        when(userService.getUserDetail(1L)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(get("/users/1/detail"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void getUserAuth() throws Exception {
        User user = User.builder().id(1L).loginId("test").password("test").status(UserStatus.ACTIVE).build();
        UserAuthResponse response = UserAuthResponse.from(user);
        when(userService.getUserAuth("test")).thenReturn(response);
        mockMvc.perform(get("/users/test/auth"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getUserAuthWithNotFound() throws Exception {
        when(userService.getUserAuth("test")).thenThrow(new UserAuthNotFoundException("test"));
        mockMvc.perform(get("/users/test/auth"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void checkUserIdentifierAvailability() throws Exception {
        when(userService.isLoginIdAvailable("test")).thenReturn(true);
        when(userService.isEmailAvailable("test@test.com")).thenReturn(true);
        mockMvc.perform(get("/users/check")
                        .queryParam("loginId", "test")
                        .queryParam("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.loginIdAvailable").value(true))
                .andExpect(jsonPath("$.data.emailAvailable").value(true))
                .andDo(print());
    }

    @Test
    void checkUserIdentifierAvailabilityWithInvalidInput() throws Exception {
        when(userService.isLoginIdAvailable("test")).thenReturn(false);
        when(userService.isEmailAvailable("test@test.com")).thenReturn(false);
        mockMvc.perform(get("/users/check")
                        .queryParam("loginId", "test")
                        .queryParam("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.loginIdAvailable").value(false))
                .andExpect(jsonPath("$.data.emailAvailable").value(false))
                .andDo(print());
    }

    @Test
    void createUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "test",
                "test",
                "test",
                "test@test.com",
                "010-1234-5678",
                LocalDate.of(2025, 1, 1)
        );
        UserResponse response = new UserResponse(
                1L,
                "test",
                "test",
                "test@test.com",
                "010-1234-5678",
                LocalDate.of(2025, 1, 1),
                0,
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(userService.createUser(request)).thenReturn(response);
        mockMvc.perform(post("/users")
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
    void createUserWithDefaultMembershipNotFound() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "test",
                "test",
                "test",
                "test@test.com",
                "010-1234-5678",
                LocalDate.of(2025, 1, 1)
        );
        when(userService.createUser(request)).thenThrow(
                new IllegalStateException("Default membership is not configured."));
        mockMvc.perform(post("/users")
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
    void updateUser() throws Exception {
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
        mockMvc.perform(put("/users/1")
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
    void updateUserWithNotFound() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest(
                "new",
                "new@new.com",
                "010-5150-5150",
                LocalDate.of(2025, 1, 2)
        );
        when(userService.updateUser(1L, request)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(put("/users/1")
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
    void updateUserPassword() throws Exception {
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("old", "new");
        doNothing().when(userService).updateUserPassword(1L, request);
        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateUserPasswordWithInvalidPassword() throws Exception {
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("invalid", "new");
        doThrow(new InvalidPasswordException()).when(userService).updateUserPassword(1L, request);
        mockMvc.perform(patch("/users/1/password")
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
    void updateUserPasswordWithNotFound() throws Exception {
        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("old", "new");
        doThrow(new UserNotFoundException(1L)).when(userService).updateUserPassword(1L, request);
        mockMvc.perform(patch("/users/1/password")
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
    void activateUser() throws Exception {
        doNothing().when(userService).activateUser(1L);
        mockMvc.perform(patch("/users/1/activate"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void activateUserWithNotFound() throws Exception {
        doThrow(new UserNotFoundException(1L)).when(userService).activateUser(1L);
        mockMvc.perform(patch("/users/1/activate"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void dormantUser() throws Exception {
        doNothing().when(userService).markAsDormantUser(1L);
        mockMvc.perform(patch("/users/1/dormant"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void dormantUserWithNotFound() throws Exception {
        doThrow(new UserNotFoundException(1L)).when(userService).markAsDormantUser(1L);
        mockMvc.perform(patch("/users/1/dormant"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void withdrawUser() throws Exception {
        doNothing().when(userService).withdrawUser(1L);
        mockMvc.perform(patch("/users/1/withdraw"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void withdrawUserWithNotFound() throws Exception {
        doThrow(new UserNotFoundException(1L)).when(userService).withdrawUser(1L);
        mockMvc.perform(patch("/users/1/withdraw"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void updateMembership() throws Exception {
        UserMembershipUpdateRequest request = new UserMembershipUpdateRequest(1L);
        doNothing().when(userService).updateMembership(1L, request);
        mockMvc.perform(patch("/users/1/membership")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateMembershipWithUserNotFound() throws Exception {
        UserMembershipUpdateRequest request = new UserMembershipUpdateRequest(1L);
        doThrow(new UserNotFoundException(1L)).when(userService).updateMembership(1L, request);
        mockMvc.perform(patch("/users/1/membership")
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
    void updateMembershipWithMembershipNotFound() throws Exception {
        UserMembershipUpdateRequest request = new UserMembershipUpdateRequest(1L);
        doThrow(new MembershipNotFoundException(1L)).when(userService).updateMembership(1L, request);
        mockMvc.perform(patch("/users/1/membership")
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
    void updateLastLogin() throws Exception {
        doNothing().when(userService).updateLastLoginAt(1L);
        mockMvc.perform(patch("/users/1/last-login"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateLastLoginWithNotFound() throws Exception {
        doThrow(new UserNotFoundException(1L)).when(userService).updateLastLoginAt(1L);
        mockMvc.perform(patch("/users/1/last-login"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deleteUserWithNotFound() throws Exception {
        doThrow(new UserNotFoundException(1L)).when(userService).deleteUser(1L);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
