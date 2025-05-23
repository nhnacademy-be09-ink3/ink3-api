package shop.ink3.api.user.point.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.point.dto.PointHistoryCreateRequest;
import shop.ink3.api.user.point.dto.PointHistoryResponse;
import shop.ink3.api.user.point.dto.PointHistoryUpdateRequest;
import shop.ink3.api.user.point.entity.PointHistory;
import shop.ink3.api.user.point.entity.PointHistoryStatus;
import shop.ink3.api.user.point.exception.PointHistoryNotFoundException;
import shop.ink3.api.user.point.repository.PointHistoryRepository;
import shop.ink3.api.user.user.dto.UserPointRequest;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {
    @Mock
    PointHistoryRepository pointHistoryRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    PointService pointService;

    @Test
    void getPointHistory() {
        User user = User.builder().id(1L).build();
        PointHistory pointHistory = PointHistory.builder()
                .id(1L)
                .user(user)
                .delta(1)
                .status(PointHistoryStatus.EARN)
                .description("test")
                .createdAt(LocalDateTime.now())
                .build();
        PointHistoryResponse response = PointHistoryResponse.from(pointHistory);
        when(pointHistoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(pointHistory));
        Assertions.assertEquals(response, pointService.getPointHistory(1L, 1L));
    }

    @Test
    void getPointHistoryWithNotFound() {
        when(pointHistoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                PointHistoryNotFoundException.class,
                () -> pointService.getPointHistory(1L, 1L)
        );
    }

    @Test
    void getPointHistoriesByUserId() {
        User user = User.builder().id(1L).build();
        List<PointHistory> pointHistories = List.of(PointHistory.builder()
                        .id(1L)
                        .user(user)
                        .delta(1)
                        .status(PointHistoryStatus.EARN)
                        .description("test")
                        .createdAt(LocalDateTime.now())
                        .build(),
                PointHistory.builder()
                        .id(2L)
                        .user(user)
                        .delta(1)
                        .status(PointHistoryStatus.EARN)
                        .description("test")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<PointHistory> page = new PageImpl<>(pointHistories, pageable, pointHistories.size());

        when(pointHistoryRepository.findAllByUserId(1L, pageable)).thenReturn(page);

        PageResponse<PointHistoryResponse> response = pointService.getPointHistoriesByUserId(1L, pageable);

        Assertions.assertEquals(2, response.content().size());
        Assertions.assertEquals(0, response.page());
        Assertions.assertEquals(2, response.size());
        Assertions.assertEquals(1, response.totalPages());
        Assertions.assertFalse(response.hasNext());
    }

    @Test
    void createPointHistory() {
        User user = User.builder().id(1L).build();
        PointHistory pointHistory = PointHistory.builder()
                .id(1L)
                .user(user)
                .delta(1)
                .status(PointHistoryStatus.EARN)
                .description("test")
                .createdAt(LocalDateTime.now())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pointHistoryRepository.save(any())).thenReturn(pointHistory);
        PointHistoryResponse response = pointService.createPointHistory(
                1L,
                new PointHistoryCreateRequest(
                        1,
                        PointHistoryStatus.EARN,
                        "test"
                )
        );
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PointHistoryResponse.from(pointHistory), response);
    }

    @Test
    void createPointHistoryWithUserNotFound() {
        PointHistoryCreateRequest request = new PointHistoryCreateRequest(1, PointHistoryStatus.EARN, "test");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> pointService.createPointHistory(1L, request));
    }

    @Test
    void updatePointHistory() {
        User user = User.builder().id(1L).build();
        PointHistory pointHistory = PointHistory.builder()
                .id(1L)
                .user(user)
                .delta(1)
                .status(PointHistoryStatus.EARN)
                .description("test")
                .createdAt(LocalDateTime.now())
                .build();
        PointHistoryUpdateRequest request = new PointHistoryUpdateRequest(2, PointHistoryStatus.CANCEL, "new");
        when(pointHistoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(pointHistory));
        when(pointHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PointHistoryResponse response = pointService.updatePointHistory(1L, 1L, request);

        Assertions.assertEquals(2, response.delta());
        Assertions.assertEquals(PointHistoryStatus.CANCEL, response.status());
        Assertions.assertEquals("new", response.description());
    }

    @Test
    void updatePointHistoryWithNotFound() {
        PointHistoryUpdateRequest request = new PointHistoryUpdateRequest(2, PointHistoryStatus.CANCEL, "new");
        when(pointHistoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                PointHistoryNotFoundException.class,
                () -> pointService.updatePointHistory(1L, 1L, request)
        );
    }

    @Test
    void deletePointHistory() {
        PointHistory pointHistory = PointHistory.builder().id(1L).build();
        when(pointHistoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(pointHistory));
        pointService.deletePointHistory(1L, 1L);
        verify(pointHistoryRepository).delete(pointHistory);
    }

    @Test
    void deletePointHistoryWithNotFound() {
        PointHistory pointHistory = PointHistory.builder().id(1L).build();
        when(pointHistoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                PointHistoryNotFoundException.class,
                () -> pointService.deletePointHistory(1L, 1L)
        );
    }

    @Test
    void earnPoint() {
        User user = User.builder().id(1L).point(0).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        pointService.earnPoint(1L, new UserPointRequest(1000));
        Assertions.assertEquals(1000, user.getPoint());
        verify(pointHistoryRepository).save(any());
    }

    @Test
    void earnPointWithNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> pointService.earnPoint(1L, new UserPointRequest(1000))
        );
    }

    @Test
    void usePoint() {
        User user = User.builder().id(1L).point(1000).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        pointService.usePoint(1L, new UserPointRequest(1000));
        Assertions.assertEquals(0, user.getPoint());
        verify(pointHistoryRepository).save(any());
    }

    @Test
    void usePointWithNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> pointService.usePoint(1L, new UserPointRequest(1000))
        );
    }

    @Test
    void cancelEarnPoint() {
        User user = User.builder().id(1L).point(1000).build();
        PointHistory pointHistory = PointHistory.builder()
                .id(1L)
                .user(user)
                .delta(1000)
                .status(PointHistoryStatus.EARN)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pointHistoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(pointHistory));
        pointService.cancelPoint(1L, 1L);
        Assertions.assertEquals(0, user.getPoint());
        verify(pointHistoryRepository).save(argThat(saved ->
                saved.getUser().equals(user)
                        && saved.getDelta() == -1000
                        && saved.getStatus() == PointHistoryStatus.CANCEL
        ));
    }
}
