package shop.ink3.api.user.point.dto;

import java.time.LocalDateTime;
import shop.ink3.api.user.point.entity.PointHistory;
import shop.ink3.api.user.point.entity.PointHistoryStatus;

public record PointHistoryResponse(
        Long id,
        Integer delta,
        PointHistoryStatus status,
        String description,
        LocalDateTime createdAt
) {
    public static PointHistoryResponse from(PointHistory pointHistory) {
        return new PointHistoryResponse(
                pointHistory.getId(),
                pointHistory.getDelta(),
                pointHistory.getStatus(),
                pointHistory.getDescription(),
                pointHistory.getCreatedAt()
        );
    }
}
