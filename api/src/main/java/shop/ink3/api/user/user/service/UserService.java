package shop.ink3.api.user.user.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.membership.exception.MembershipNotFoundException;
import shop.ink3.api.user.membership.repository.MembershipRepository;
import shop.ink3.api.user.point.entity.PointHistory;
import shop.ink3.api.user.point.entity.PointHistoryStatus;
import shop.ink3.api.user.point.repository.PointHistoryRepository;
import shop.ink3.api.user.user.dto.UserAuthResponse;
import shop.ink3.api.user.user.dto.UserCreateRequest;
import shop.ink3.api.user.user.dto.UserDetailResponse;
import shop.ink3.api.user.user.dto.UserMembershipUpdateRequest;
import shop.ink3.api.user.user.dto.UserPointRequest;
import shop.ink3.api.user.user.dto.UserResponse;
import shop.ink3.api.user.user.dto.UserUpdateRequest;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;
import shop.ink3.api.user.user.exception.InsufficientPointException;
import shop.ink3.api.user.user.exception.UserAuthNotFoundException;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return UserDetailResponse.from(user);
    }

    public UserAuthResponse getUserAuth(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserAuthNotFoundException(loginId));
        return UserAuthResponse.from(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        Membership defaultMembership = membershipRepository.findByIsDefault(true)
                .orElseThrow(() -> new IllegalStateException("Default membership is not configured."));
        User user = User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .birthday(request.birthday())
                .point(0)
                .membership(defaultMembership)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.update(request.password(), request.name(), request.email(), request.phone(), request.birthday());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void activateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.activate();
        userRepository.save(user);
    }

    @Transactional
    public void markAsDormant(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.markAsDormant();
        userRepository.save(user);
    }

    @Transactional
    public void withdraw(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.withdraw();
        userRepository.save(user);
    }

    @Transactional
    public void earnPoint(long userId, UserPointRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.earnPoint(request.amount());
        userRepository.save(user);
        pointHistoryRepository.save(
                PointHistory.builder()
                        .user(user)
                        .delta(request.amount())
                        .status(PointHistoryStatus.EARN)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public void usePoint(long userId, UserPointRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (user.getPoint() < request.amount()) {
            throw new InsufficientPointException();
        }
        user.usePoint(request.amount());
        userRepository.save(user);
        pointHistoryRepository.save(
                PointHistory.builder()
                        .user(user)
                        .delta(request.amount())
                        .status(PointHistoryStatus.USE)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public void updateMembership(long userId, UserMembershipUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Membership membership = membershipRepository.findById(request.membershipId())
                .orElseThrow(() -> new MembershipNotFoundException(request.membershipId()));
        user.updateMembership(membership);
        userRepository.save(user);
    }

    @Transactional
    public void updateLastLoginAt(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.updateLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }
}
