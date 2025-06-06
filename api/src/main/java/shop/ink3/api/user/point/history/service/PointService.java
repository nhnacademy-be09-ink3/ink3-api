package shop.ink3.api.user.point.history.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.point.history.dto.PointHistoryCreateRequest;
import shop.ink3.api.user.point.history.dto.PointHistoryResponse;
import shop.ink3.api.user.point.history.dto.PointHistoryUpdateRequest;
import shop.ink3.api.user.point.history.entity.PointHistory;
import shop.ink3.api.user.point.history.entity.PointHistoryStatus;
import shop.ink3.api.user.point.history.exception.PointHistoryAlreadyCanceledException;
import shop.ink3.api.user.point.history.exception.PointHistoryNotFoundException;
import shop.ink3.api.user.point.history.repository.PointHistoryRepository;
import shop.ink3.api.user.user.dto.UserPointRequest;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.InsufficientPointException;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PointService {
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PointHistoryResponse getPointHistory(long userId, long pointHistoryId) {
        PointHistory pointHistory = pointHistoryRepository.findByIdAndUserId(userId, pointHistoryId)
                .orElseThrow(() -> new PointHistoryNotFoundException(pointHistoryId));
        return PointHistoryResponse.from(pointHistory);
    }

    @Transactional(readOnly = true)
    public PageResponse<PointHistoryResponse> getPointHistoriesByUserId(long userId, Pageable pageable) {
        Page<PointHistory> pointHistories = pointHistoryRepository.findAllByUserId(userId, pageable);
        return PageResponse.from(pointHistories.map(PointHistoryResponse::from));
    }

    public PointHistoryResponse createPointHistory(long userId, PointHistoryCreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        PointHistory pointHistory = PointHistory.builder()
                .user(user)
                .delta(request.delta())
                .status(request.status())
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();
        return PointHistoryResponse.from(pointHistoryRepository.save(pointHistory));
    }

    public PointHistoryResponse updatePointHistory(
            long userId,
            long pointHistoryId,
            PointHistoryUpdateRequest request
    ) {
        PointHistory pointHistory = pointHistoryRepository.findByIdAndUserId(userId, pointHistoryId)
                .orElseThrow(() -> new PointHistoryNotFoundException(pointHistoryId));
        pointHistory.update(request.delta(), request.status(), request.description());
        return PointHistoryResponse.from(pointHistoryRepository.save(pointHistory));
    }

    public void deletePointHistory(long userId, long pointHistoryId) {
        PointHistory pointHistory = pointHistoryRepository.findByIdAndUserId(userId, pointHistoryId)
                .orElseThrow(() -> new PointHistoryNotFoundException(pointHistoryId));
        pointHistoryRepository.delete(pointHistory);
    }

    public PointHistory earnPoint(long userId, UserPointRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.earnPoint(request.amount());
        userRepository.save(user);
        return pointHistoryRepository.save(
                PointHistory.builder()
                        .user(user)
                        .delta(request.amount())
                        .status(PointHistoryStatus.EARN)
                        .description(request.description())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public PointHistory usePoint(long userId, UserPointRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        log.info("usePoint={},  used amount= {}", user.getPoint(), request.amount());
        if (user.getPoint() < request.amount()) {
            throw new InsufficientPointException();
        }
        user.usePoint(request.amount());
        userRepository.save(user);
        return pointHistoryRepository.save(
                PointHistory.builder()
                        .user(user)
                        .delta(-request.amount())
                        .status(PointHistoryStatus.USE)
                        .description(request.description())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public void cancelPoint(long userId, long pointHistoryId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (pointHistoryRepository.existsByOriginId(pointHistoryId)) {
            throw new PointHistoryAlreadyCanceledException(pointHistoryId);
        }

        PointHistory pointHistory = pointHistoryRepository.findByIdAndUserId(pointHistoryId, userId)
                .orElseThrow(() -> new PointHistoryNotFoundException(pointHistoryId));

        if (pointHistory.getStatus() == PointHistoryStatus.CANCEL) {
            throw new PointHistoryAlreadyCanceledException(pointHistoryId);
        }

        user.cancelPoint(pointHistory.getDelta());
        userRepository.save(user);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user)
                .delta(-pointHistory.getDelta())
                .origin(pointHistory)
                .status(PointHistoryStatus.CANCEL)
                .description(pointHistory.getDescription() + " 취소")
                .createdAt(LocalDateTime.now())
                .build());
    }
}
