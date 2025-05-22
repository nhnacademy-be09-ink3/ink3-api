package shop.ink3.api.user.point.eventListener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.user.point.dto.PointHistoryCreateRequest;
import shop.ink3.api.user.point.dto.PointHistoryResponse;
import shop.ink3.api.user.point.entity.PointHistoryStatus;
import shop.ink3.api.user.point.service.PointService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final PointService pointService;
    private final OrderService orderService;
    private static final String POINT_PAYMENT_DESCRIPTION = "도서 결제에 의한 적립";

    @Async
    @Transactional
    @EventListener
    public void handlePointHistoryAfterPayment(PointHistoryAfterPaymentEven event) {
        try {
            //TODO 포인트 정책 확인 후 포인트 CreateRequest 생성 -> 적립량 고려
            int pointAmount = event.amount();
            PointHistoryCreateRequest request = new PointHistoryCreateRequest(pointAmount,
                    PointHistoryStatus.EARN, POINT_PAYMENT_DESCRIPTION);
            PointHistoryResponse pointHistoryResponse = pointService.createPointHistory(event.orderId(), request);
            orderService.savePointHistoryInOrder(event.orderId(), pointHistoryResponse.id());
        } catch (Exception e) {
            log.error("포인트 적립 실패: {}", e.getMessage());
        }
    }

    @Async
    @Transactional
    @EventListener
    public void handlePointHistoryAfterCancelPayment(PointHistoryAfterCancelPaymentEven event) {
        pointService.cancelPoint(event.orderId(), event.pointHistoryId());
    }
}
