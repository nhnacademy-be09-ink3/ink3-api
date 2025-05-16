package shop.ink3.api.user.user.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.user.common.exception.DormantException;
import shop.ink3.api.user.common.exception.InvalidPasswordException;
import shop.ink3.api.user.common.exception.WithdrawnException;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.membership.exception.MembershipNotFoundException;
import shop.ink3.api.user.membership.repository.MembershipRepository;
import shop.ink3.api.user.point.entity.PointHistory;
import shop.ink3.api.user.point.entity.PointHistoryStatus;
import shop.ink3.api.user.point.repository.PointHistoryRepository;
import shop.ink3.api.user.social.entity.Social;
import shop.ink3.api.user.social.repository.SocialRepository;
import shop.ink3.api.user.user.dto.SocialUserCreateRequest;
import shop.ink3.api.user.user.dto.UserAuthResponse;
import shop.ink3.api.user.user.dto.UserCreateRequest;
import shop.ink3.api.user.user.dto.UserDetailResponse;
import shop.ink3.api.user.user.dto.UserMembershipUpdateRequest;
import shop.ink3.api.user.user.dto.UserPasswordUpdateRequest;
import shop.ink3.api.user.user.dto.UserPointRequest;
import shop.ink3.api.user.user.dto.UserResponse;
import shop.ink3.api.user.user.dto.UserUpdateRequest;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;
import shop.ink3.api.user.user.exception.InsufficientPointException;
import shop.ink3.api.user.user.exception.SocialUserAuthNotFoundException;
import shop.ink3.api.user.user.exception.UserAuthNotFoundException;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final SocialRepository socialRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return UserDetailResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserAuthResponse getUserAuth(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserAuthNotFoundException(loginId));
        if (user.getStatus() == UserStatus.DORMANT) {
            throw new DormantException(user.getId());
        }
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new WithdrawnException(user.getId());
        }
        return UserAuthResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserAuthResponse getSocialUserAuth(String provider, String providerUserId) {
        User user = socialRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .orElseThrow(() -> new SocialUserAuthNotFoundException(provider, providerUserId)).getUser();
        if (user.getStatus() == UserStatus.DORMANT) {
            throw new DormantException(user.getId());
        }
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new WithdrawnException(user.getId());
        }
        return UserAuthResponse.from(user);
    }

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

    public UserResponse createSocialUser(SocialUserCreateRequest request) {
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
        user = userRepository.save(user);

        Social social = Social.builder()
                .user(user)
                .provider(request.provider())
                .providerUserId(request.providerUserId())
                .build();
        socialRepository.save(social);

        return UserResponse.from(user);
    }

    public UserResponse updateUser(long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.update(request.name(), request.email(), request.phone(), request.birthday());
        return UserResponse.from(userRepository.save(user));
    }

    public void updateUserPassword(long userId, UserPasswordUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void activateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.activate();
        userRepository.save(user);
    }

    public void markAsDormantUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.markAsDormant();
        userRepository.save(user);
    }

    public void withdrawUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.withdraw();
        userRepository.save(user);
    }

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

    public void updateMembership(long userId, UserMembershipUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Membership membership = membershipRepository.findById(request.membershipId())
                .orElseThrow(() -> new MembershipNotFoundException(request.membershipId()));
        user.updateMembership(membership);
        userRepository.save(user);
    }

    public void updateLastLoginAt(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.updateLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deleteUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }
}
