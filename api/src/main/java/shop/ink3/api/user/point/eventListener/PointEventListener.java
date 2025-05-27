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
import shop.ink3.api.user.user.dto.UserPointRequest;

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
            //TODO 포인트 정책 적용 필요
            int temp_PointAccumulateRate = 10;
            int pointAmount = event.paymentAmount()/temp_PointAccumulateRate;
            pointService.earnPoint(1,new UserPointRequest(pointAmount));
            pointService.usePoint(1, new UserPointRequest(event.paymentAmount()));
        } catch (Exception e) {
            log.error("포인트 적립 실패: {}", e.getMessage());
        }
    }

    @Async
    @Transactional
    @EventListener
    public void handlePointHistoryAfterCancelPayment(PointHistoryAfterCancelPaymentEven event) {
        try {
            pointService.cancelPoint(event.orderId(), event.pointHistoryId());
        }catch (Exception e) {
            log.error("포인트 취소 실패: {}", e.getMessage());
        }
    }
}
