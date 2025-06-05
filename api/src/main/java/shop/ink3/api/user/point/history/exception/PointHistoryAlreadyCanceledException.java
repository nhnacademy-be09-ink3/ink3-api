package shop.ink3.api.user.point.history.exception;

public class PointHistoryAlreadyCanceledException extends RuntimeException {
    public PointHistoryAlreadyCanceledException(long pointHistoryId) {
        super("This point transaction has already been canceled. ID: %d".formatted(pointHistoryId));
    }
}
