package shop.ink3.api.user.point.history.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class PointHistoryNotFoundException extends NotFoundException {
    public PointHistoryNotFoundException(long pointHistoryId) {
        super("PointHistory not found. ID: %d".formatted(pointHistoryId));
    }
}
