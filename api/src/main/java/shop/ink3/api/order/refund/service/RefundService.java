package shop.ink3.api.order.refund.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.order.refund.dto.RefundCreateRequest;
import shop.ink3.api.order.refund.dto.RefundResponse;
import shop.ink3.api.order.refund.dto.RefundUpdateRequest;
import shop.ink3.api.order.refund.entity.Refund;
import shop.ink3.api.order.refund.exception.RefundNotFoundException;
import shop.ink3.api.order.refund.repository.RefundRepository;
import shop.ink3.api.order.refundPolicy.entity.RefundPolicy;
import shop.ink3.api.order.refundPolicy.exception.RefundPolicyNotFoundException;

@RequiredArgsConstructor
@Service
public class RefundService {
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;

    // 생성
    @Transactional
    public RefundResponse createRefund(RefundCreateRequest request) {
        Optional<Order> optionalOrder = orderRepository.findById(request.getOrderId());
        if(optionalOrder.isEmpty()){
            throw new OrderNotFoundException(request.getOrderId());
        }
        Order order = optionalOrder.get();
        Refund refund = Refund.builder()
                .id(0L)
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
        Page<RefundResponse> pageRefundResponse = pageRefund.map(
                refund -> RefundResponse.from(refund));
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
    private Refund getRefundOrThrow(long refundId) {
        Optional<Refund> optionalRefund = refundRepository.findById(refundId);
        if (!optionalRefund.isPresent()) {
            throw new RefundNotFoundException(refundId);
        }
        return optionalRefund.get();
    }
}
