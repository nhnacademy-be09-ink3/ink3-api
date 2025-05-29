package shop.ink3.api.user.point.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.point.dto.PointHistoryCreateRequest;
import shop.ink3.api.user.point.dto.PointHistoryResponse;
import shop.ink3.api.user.point.dto.PointHistoryUpdateRequest;
import shop.ink3.api.user.point.service.PointService;
import shop.ink3.api.user.user.dto.UserPointRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/points")
public class PointController {
    private final PointService pointService;

    @GetMapping("/{pointHistoryId}")
    public ResponseEntity<CommonResponse<PointHistoryResponse>> getPointHistory(
            @PathVariable long userId,
            @PathVariable long pointHistoryId
    ) {
        return ResponseEntity.ok(CommonResponse.success(pointService.getPointHistory(userId, pointHistoryId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PointHistoryResponse>>> getPointHistories(
            @PathVariable long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                CommonResponse.success(pointService.getPointHistoriesByUserId(userId, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<PointHistoryResponse>> createPointHistory(
            @PathVariable long userId,
            @RequestBody @Valid PointHistoryCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.create(pointService.createPointHistory(userId, request)));
    }

    @PutMapping("/{pointHistoryId}")
    public ResponseEntity<CommonResponse<PointHistoryResponse>> updatePointHistory(
            @PathVariable long userId,
            @PathVariable long pointHistoryId,
            @RequestBody @Valid PointHistoryUpdateRequest request
    ) {
        return ResponseEntity.ok(
                CommonResponse.update(pointService.updatePointHistory(userId, pointHistoryId, request))
        );
    }

    @DeleteMapping("/{pointHistoryId}")
    public ResponseEntity<Void> deletePointHistory(
            @PathVariable long userId,
            @PathVariable long pointHistoryId
    ) {
        pointService.deletePointHistory(userId, pointHistoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/earn")
    public ResponseEntity<Void> earnPoints(@PathVariable long userId, @RequestBody @Valid UserPointRequest request) {
        pointService.earnPoint(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/use")
    public ResponseEntity<Void> usePoints(@PathVariable long userId, @RequestBody @Valid UserPointRequest request) {
        pointService.usePoint(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{pointHistoryId}/cancel")
    public ResponseEntity<Void> cancelPoints(@PathVariable long userId, @PathVariable long pointHistoryId) {
        pointService.cancelPoint(userId, pointHistoryId);
        return ResponseEntity.noContent().build();
    }
}
