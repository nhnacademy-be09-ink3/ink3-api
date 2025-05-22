package shop.ink3.api.user.user.controller;

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
import shop.ink3.api.user.user.dto.UserDetailResponse;
import shop.ink3.api.user.user.dto.UserPasswordUpdateRequest;
import shop.ink3.api.user.user.dto.UserResponse;
import shop.ink3.api.user.user.dto.UserUpdateRequest;
import shop.ink3.api.user.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/me")
public class MeUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<CommonResponse<UserResponse>> getCurrentUser(@RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUser(userId)));
    }

    @GetMapping("/detail")
    public ResponseEntity<CommonResponse<UserDetailResponse>> getCurrentUserDetail(
            @RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(CommonResponse.success(userService.getUserDetail(userId)));
    }

    @PutMapping
    public ResponseEntity<CommonResponse<UserResponse>> updateCurrentUser(
            @RequestHeader("X-User-Id") long userId,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.update(userService.updateUser(userId, request)));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updateCurrentUserPassword(
            @RequestHeader("X-User-Id") long userId,
            @RequestBody @Valid UserPasswordUpdateRequest request
    ) {
        userService.updateUserPassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<Void> withdrawCurrentUser(@RequestHeader("X-User-Id") long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.noContent().build();
    }
}
