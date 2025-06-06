package shop.ink3.api.user.user.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.social.dto.SocialUserResponse;
import shop.ink3.api.user.user.dto.IdentifierAvailabilityResponse;
import shop.ink3.api.user.user.dto.SocialUserCreateRequest;
import shop.ink3.api.user.user.dto.UserAuthResponse;
import shop.ink3.api.user.user.dto.UserCreateRequest;
import shop.ink3.api.user.user.dto.UserDetailResponse;
import shop.ink3.api.user.user.dto.UserListItemDto;
import shop.ink3.api.user.user.dto.UserMembershipUpdateRequest;
import shop.ink3.api.user.user.dto.UserPasswordUpdateRequest;
import shop.ink3.api.user.user.dto.UserResponse;
import shop.ink3.api.user.user.dto.UserStatisticsResponse;
import shop.ink3.api.user.user.dto.UserStatusResponse;
import shop.ink3.api.user.user.dto.UserUpdateRequest;
import shop.ink3.api.user.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserResponse>> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUser(userId)));
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<CommonResponse<UserDetailResponse>> getUserDetail(@PathVariable long userId) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUserDetail(userId)));
    }

    @GetMapping("/{loginId}/auth")
    public ResponseEntity<CommonResponse<UserAuthResponse>> getUserAuth(@PathVariable String loginId) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUserAuth(loginId)));
    }

    @GetMapping("/social/{provider}/{providerUserId}")
    public ResponseEntity<CommonResponse<SocialUserResponse>> getSocialUser(
            @PathVariable String provider,
            @PathVariable String providerUserId
    ) {
        return ResponseEntity.ok(CommonResponse.success(userService.getSocialUser(provider, providerUserId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<UserListItemDto>>> getUsers(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUsersForManagement(keyword, pageable)));
    }

    @GetMapping(params = "birthday")
    public ResponseEntity<CommonResponse<List<UserResponse>>> getUsersByBirthday(
            @RequestParam LocalDate birthday
    ) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUsersByBirthday(birthday)));
    }

    @GetMapping("/statistics")
    public ResponseEntity<CommonResponse<UserStatisticsResponse>> getUserStatistics() {
        return ResponseEntity.ok(CommonResponse.success(userService.getUserStatistics()));
    }

    @GetMapping("/status")
    public ResponseEntity<CommonResponse<UserStatusResponse>> getUserStatus(@RequestParam String loginId) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUserStatus(loginId)));
    }

    @GetMapping(value = "/available", params = "loginId")
    public ResponseEntity<CommonResponse<IdentifierAvailabilityResponse>> checkLoginIdAvailable(
            @RequestParam String loginId
    ) {
        return ResponseEntity.ok(CommonResponse.success(userService.isLoginIdAvailable(loginId)));
    }

    @GetMapping(value = "/available", params = "email")
    public ResponseEntity<CommonResponse<IdentifierAvailabilityResponse>> checkEmailAvailable(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(CommonResponse.success(userService.isEmailAvailable(email)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<UserResponse>> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(userService.createUser(request)));
    }

    @PostMapping("/social")
    public ResponseEntity<CommonResponse<UserResponse>> createSocialUser(
            @RequestBody @Valid SocialUserCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.create(userService.createSocialUser(request)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserResponse>> updateUser(
            @PathVariable long userId,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.update(userService.updateUser(userId, request)));
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable long userId,
            @RequestBody @Valid UserPasswordUpdateRequest request
    ) {
        userService.updateUserPassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable long userId) {
        userService.activateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/login-id/{loginId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String loginId) {
        userService.activateUser(loginId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/dormant")
    public ResponseEntity<Void> dormantUser(@PathVariable long userId) {
        userService.markAsDormantUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/withdraw")
    public ResponseEntity<Void> withdrawUser(@PathVariable long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/membership")
    public ResponseEntity<Void> updateMembership(
            @PathVariable long userId,
            @RequestBody @Valid UserMembershipUpdateRequest request
    ) {
        userService.updateMembership(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable long userId) {
        userService.updateLastLoginAt(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
