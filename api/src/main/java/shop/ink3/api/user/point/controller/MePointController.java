package shop.ink3.api.user.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.point.dto.PointHistoryResponse;
import shop.ink3.api.user.point.service.PointService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/me/points")
public class MePointController {
    private final PointService pointService;

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PointHistoryResponse>>> getCurrentUserPointHistories(
            @RequestHeader("X-User-Id") long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                CommonResponse.success(pointService.getPointHistoriesByUserId(userId, pageable)));
    }
}
