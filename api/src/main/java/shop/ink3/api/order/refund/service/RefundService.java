package shop.ink3.api.order.refund.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.refund.dto.RefundCreateRequest;
import shop.ink3.api.order.refund.dto.RefundResponse;
import shop.ink3.api.order.refund.dto.RefundUpdateRequest;
import shop.ink3.api.order.refund.entity.Refund;
import shop.ink3.api.order.refund.exception.RefundNotFoundException;
import shop.ink3.api.order.refund.exception.ReturnDeadlineExceededException;
import shop.ink3.api.order.refund.repository.RefundRepository;
import shop.ink3.api.order.refundPolicy.entity.RefundPolicy;
import shop.ink3.api.order.refundPolicy.exception.RefundPolicyNotFoundException;
import shop.ink3.api.order.refundPolicy.repository.RefundPolicyRepository;
import shop.ink3.api.order.shipment.entity.Shipment;
import shop.ink3.api.order.shipment.exception.ShipmentNotFoundException;
import shop.ink3.api.order.shipment.repository.ShipmentRepository;
import shop.ink3.api.order.shipment.service.ShipmentService;

@RequiredArgsConstructor
@Service
public class RefundService {
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private static final String refundDefectReason = "파손";
    private static final String refundBasicReason = "일반";

    // 반품 처리 가능 여부
    public void availableRefund(RefundCreateRequest request){
        RefundPolicy refundPolicy = refundPolicyRepository.findByIsAvailableTrue();
        Shipment shipment = shipmentRepository.findByOrder_Id(request.getOrderId())
                .orElseThrow(ShipmentNotFoundException::new);
        orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));

        LocalDate deliveredDate = shipment.getDeliveredAt().toLocalDate();
        LocalDate today = LocalDate.now();
        int daysSinceDelivery = (int) ChronoUnit.DAYS.between(deliveredDate, today);
        if(request.getReason().trim().equals(refundDefectReason)){
            if(refundPolicy.getDefectReturnDeadLine() < daysSinceDelivery){
                throw new ReturnDeadlineExceededException();
            }
        }else if(request.getReason().trim().equals(refundBasicReason)){
            if(refundPolicy.getReturnDeadLine() < daysSinceDelivery){
                throw new ReturnDeadlineExceededException();
            }
        }
    }

    // 반품 생성
    @Transactional(propagation = Propagation.REQUIRED)
    public RefundResponse createRefund(RefundCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));
        Refund refund = Refund.builder()
                .order(order)
                .details(request.getDetails())
                .reason(request.getReason())
                .build();
        return RefundResponse.from(refundRepository.save(refund));
    }

    // 주문 id에 대한 조회
    public RefundResponse getOrderRefund(long orderId){
        Refund refund = getRefundOrThrow(orderId);
        return RefundResponse.from(refund);
    }

    // 사용자에 대한 반품 list 조회
    public PageResponse<RefundResponse> getUserRefundList(long userId, Pageable pageable) {
        Page<Refund> pageRefund = refundRepository.findByOrder_UserId(userId, pageable);
        Page<RefundResponse> pageRefundResponse = pageRefund.map(RefundResponse::from);
        return PageResponse.from(pageRefundResponse);
    }

    // 주문 id에 대한 수정
    @Transactional
    public RefundResponse updateRefund(long orderId,RefundUpdateRequest request) {
        Refund refund = getRefundOrThrow(orderId);
        refund.update(request);
        return RefundResponse.from(refundRepository.save(refund));
    }

    // 주문 Id에 대한 삭제
    @Transactional
    public void deleteRefund(long orderId) {
        getRefundOrThrow(orderId);
        refundRepository.deleteById(orderId);
    }

    // 조회 로직
    private Refund getRefundOrThrow(long orderId) {
        Optional<Refund> optionalRefund = refundRepository.findByOrder_Id(orderId);
        if (optionalRefund.isEmpty()) {
            throw new RefundNotFoundException(orderId);
        }
        return optionalRefund.get();
    }
}
