package shop.ink3.api.user.point.history.eventListener;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import shop.ink3.api.order.orderPoint.service.OrderPointService;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.membership.service.MembershipService;
import shop.ink3.api.user.point.history.entity.PointHistory;
import shop.ink3.api.user.point.history.service.PointService;
import shop.ink3.api.user.point.policy.dto.PointPolicyResponse;
import shop.ink3.api.user.point.policy.service.PointPolicyService;
import shop.ink3.api.user.user.dto.UserPointRequest;
import shop.ink3.api.user.user.dto.UserResponse;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;
import shop.ink3.api.user.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private static final String POINT_PAYMENT_DESCRIPTION_EARN = "도서 결제에 의한 적립";
    private static final String POINT_PAYMENT_DESCRIPTION_USE = "도서 결제 시 포인트 사용";
    private final UserRepository userRepository;
    private final PointService pointService;
    private final PointPolicyService pointPolicyService;
    private final OrderPointService orderPointService;

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointHistoryAfterPayment(PointHistoryAfterPaymentEven event) {
        boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("[트랜잭션 상태 확인] 트랜잭션 활성 여부: {}", isActive);

        try {
            User user = userRepository.findById(event.userId()).orElseThrow(() -> new UserNotFoundException(event.userId()));

            int membershipPointRate = user.getMembership().getPointRate();
            int pointPolicyRate = pointPolicyService.getPointPolicy(1).defaultRate();
            int totalPointRate = membershipPointRate + pointPolicyRate;
            int pointAmount = (event.paymentAmount() * totalPointRate) / 100;

            PointHistory earnPointHistory = pointService.earnPoint(event.userId(),
                    new UserPointRequest(pointAmount, POINT_PAYMENT_DESCRIPTION_EARN));
            orderPointService.createOrderPoint(event.orderId(), earnPointHistory);

            if (event.usedPointAmount() > 0) {
                PointHistory usePointHistory = pointService.usePoint(event.userId(),
                        new UserPointRequest(event.usedPointAmount(), POINT_PAYMENT_DESCRIPTION_USE));
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
