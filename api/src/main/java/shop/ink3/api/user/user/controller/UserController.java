package shop.ink3.api.user.user.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
import shop.ink3.api.user.user.dto.SocialUserCreateRequest;
import shop.ink3.api.user.user.dto.UserAuthResponse;
import shop.ink3.api.user.user.dto.UserCreateRequest;
import shop.ink3.api.user.user.dto.UserDetailResponse;
import shop.ink3.api.user.user.dto.UserMembershipUpdateRequest;
import shop.ink3.api.user.user.dto.UserPasswordUpdateRequest;
import shop.ink3.api.user.user.dto.UserResponse;
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

    @GetMapping("/auth/{loginId}")
    public ResponseEntity<CommonResponse<UserAuthResponse>> getUserAuth(@PathVariable String loginId) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUserAuth(loginId)));
    }

    @GetMapping("/auth/social/{provider}/{providerUserId}")
    public ResponseEntity<CommonResponse<UserAuthResponse>> getSocialUserAuth(
            @PathVariable String provider,
            @PathVariable String providerUserId
    ) {
        return ResponseEntity.ok(CommonResponse.success(userService.getSocialUserAuth(provider, providerUserId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<UserResponse>>> getUsersByBirthday(
            @RequestParam LocalDate birthday
    ) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUsersByBirthday(birthday)));
    }

    @GetMapping("/check")
    public ResponseEntity<CommonResponse<Map<String, Boolean>>> checkUserIdentifierAvailability(
            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String email
    ) {
        Map<String, Boolean> result = new HashMap<>();
        if (Objects.nonNull(loginId)) {
            result.put("loginIdAvailable", userService.isLoginIdAvailable(loginId));
        }
        if (Objects.nonNull(email)) {
            result.put("emailAvailable", userService.isEmailAvailable(email));
        }
        return ResponseEntity.ok(CommonResponse.success(result));
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

    @PatchMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable long userId) {
        userService.activateUser(userId);
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
