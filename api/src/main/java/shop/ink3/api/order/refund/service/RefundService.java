package shop.ink3.api.order.refund.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

@Transactional
@RequiredArgsConstructor
@Service
public class RefundService {
    private static final String REFUND_DEFECT_REASON = "파손";
    private static final String REFUND_BASIC_REASON = "일반";
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final RefundPolicyRepository refundPolicyRepository;

    // 반품 생성
    public RefundResponse createRefund(RefundCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));
        Refund refund = Refund.builder()
                .order(order)
                .details(request.getDetails())
                .reason(request.getReason())
                .RefundShippingFee(request.getRefundShippingFee())
                .createdAt(LocalDateTime.now())
                .build();
        return RefundResponse.from(refundRepository.save(refund));
    }

    // 주문 id에 대한 조회
    @Transactional(readOnly = true)
    public RefundResponse getOrderRefund(long orderId) {
        Refund refund = getRefundOrThrow(orderId);
        return RefundResponse.from(refund);
    }

    // 사용자에 대한 반품 list 조회
    @Transactional(readOnly = true)
    public PageResponse<RefundResponse> getUserRefundList(long userId, Pageable pageable) {
        Page<Refund> pageRefund = refundRepository.findAllByOrderUserId(userId, pageable);
        Page<RefundResponse> pageRefundResponse = pageRefund.map(RefundResponse::from);
        return PageResponse.from(pageRefundResponse);
    }

    // 주문 id에 대한 수정
    public RefundResponse updateRefund(long orderId, RefundUpdateRequest request) {
        Refund refund = getRefundOrThrow(orderId);
        refund.update(request);
        return RefundResponse.from(refundRepository.save(refund));
    }

    // 주문 Id에 대한 삭제
    public void deleteRefund(long orderId) {
        getRefundOrThrow(orderId);
        refundRepository.deleteById(orderId);
    }

    // 조회 로직
    protected Refund getRefundOrThrow(long orderId) {
        Optional<Refund> optionalRefund = refundRepository.findByOrderId(orderId);
        if (optionalRefund.isEmpty()) {
            throw new RefundNotFoundException(orderId);
        }
        return optionalRefund.get();
    }

    // 반품 처리 가능 여부
    public void availableRefund(RefundCreateRequest request) {
        RefundPolicy refundPolicy = refundPolicyRepository.findByIsAvailableTrue();
        Shipment shipment = shipmentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(ShipmentNotFoundException::new);
        orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));
        LocalDate deliveredDate = shipment.getDeliveredAt().toLocalDate();
        LocalDate today = LocalDate.now();
        int daysSinceDelivery = (int) ChronoUnit.DAYS.between(deliveredDate, today);
        if (request.getReason().trim().equals(REFUND_DEFECT_REASON)) {
            if (refundPolicy.getDefectReturnDeadLine() < daysSinceDelivery) {
                throw new ReturnDeadlineExceededException();
            }
        } else if (request.getReason().trim().equals(REFUND_BASIC_REASON)) {
            if (refundPolicy.getReturnDeadLine() < daysSinceDelivery) {
                throw new ReturnDeadlineExceededException();
            }
        }
    }
}
