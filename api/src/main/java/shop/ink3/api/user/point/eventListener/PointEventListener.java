package shop.ink3.api.user.point.eventListener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.user.point.service.PointService;
import shop.ink3.api.user.user.dto.UserPointRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private static final String POINT_PAYMENT_DESCRIPTION_EARN = "도서 결제에 의한 적립";
    private static final String POINT_PAYMENT_DESCRIPTION_USE = "도서 결제 시 포인트 사용";
    private final PointService pointService;

    @Async
    @Transactional
    @EventListener
    public void handlePointHistoryAfterPayment(PointHistoryAfterPaymentEven event) {
        try {
            //TODO 포인트 정책 적용 필요
            int temp_PointAccumulateRate = 10;
            int pointAmount = event.paymentAmount()/temp_PointAccumulateRate;
            pointService.earnPoint(1,new UserPointRequest(pointAmount,POINT_PAYMENT_DESCRIPTION_EARN));
            pointService.usePoint(1, new UserPointRequest(event.paymentAmount(),POINT_PAYMENT_DESCRIPTION_USE));
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
