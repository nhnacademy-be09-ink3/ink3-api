package shop.ink3.api.user.point.eventListener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.ink3.api.order.orderPoint.service.OrderPointService;
import shop.ink3.api.user.point.entity.PointHistory;
import shop.ink3.api.user.point.service.PointService;
import shop.ink3.api.user.user.dto.UserPointRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private static final String POINT_PAYMENT_DESCRIPTION_EARN = "도서 결제에 의한 적립";
    private static final String POINT_PAYMENT_DESCRIPTION_USE = "도서 결제 시 포인트 사용";
    private final PointService pointService;
    private final OrderPointService orderPointService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointHistoryAfterPayment(PointHistoryAfterPaymentEven event) {
        try {
            //TODO 포인트 정책 적용 필요
            int temp_PointAccumulateRate = 100;
            int pointAmount = event.paymentAmount()/temp_PointAccumulateRate;

            PointHistory earnPointHistory = pointService.earnPoint(event.userId(), new UserPointRequest(pointAmount, POINT_PAYMENT_DESCRIPTION_EARN));
            orderPointService.createOrderPoint(event.orderId(), earnPointHistory);

            if(event.usedPointAmount()>0){
                PointHistory usePointHistory = pointService.usePoint(event.userId(), new UserPointRequest(event.usedPointAmount(), POINT_PAYMENT_DESCRIPTION_USE));
                orderPointService.createOrderPoint(event.orderId(), usePointHistory);
            }

        } catch (Exception e) {
            log.error("포인트 적립 및 사용 실패: {}", e.getMessage());
        }
    }

/*    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointHistoryAfterCancelPayment(PointHistoryAfterCancelPaymentEven event) {
        try {
            for(OrderPoint orderPoint : event.orderPoints()) {
                pointService.cancelPoint(event.orderId(), orderPoint.getPointHistory().getId());
            }
        }catch (Exception e) {
            log.error("포인트 취소 실패: {}", e.getMessage());
        }
    }*/
}
