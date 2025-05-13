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

@RequiredArgsConstructor
@Service
public class RefundService {
    private final RefundRepository refundRepository;
    private final OrderService orderService;

    // 주문 id에 대한 조회
    public RefundResponse getOrderRefund(long orderId){
        Optional<Refund> optionalRefund = refundRepository.findByOrder_Id(orderId);
        if (!optionalRefund.isPresent()) {
            throw new RefundNotFoundException();
        }

        return RefundResponse.from(optionalRefund.get());
    }

    // 사용자에 대한 반품 list 조회
    public PageResponse<RefundResponse> getUserRefundList(long userId, Pageable pageable) {
        Page<Refund> pageRefund = refundRepository.findByOrder_UserId(userId, pageable);
        Page<RefundResponse> pageRefundResponse = pageRefund.map(
                refund -> RefundResponse.from(refund));
        return PageResponse.from(pageRefundResponse);
    }

    // 생성
    @Transactional
    public RefundResponse createRefund(RefundCreateRequest request) {
        OrderResponse response = orderService.getOrder(request.getOrderId());
        Refund refund = Refund.builder()
                .id(0L)
                .order(OrderResponse.getOrder(response))
                .details(request.getDetails())
                .reason(request.getReason())
                .build();

        return RefundResponse.from(refundRepository.save(refund));
    }

    // 주문 id에 대한 수정
    @Transactional
    public RefundResponse updateRefund(long orderId,RefundUpdateRequest request) {
        Optional<Refund> optionalRefund = refundRepository.findByOrder_Id(orderId);
        if(!optionalRefund.isPresent()){
            throw new RefundNotFoundException();
        }
        Refund refund = optionalRefund.get();

        refund.update(request);
        return RefundResponse.from(refundRepository.save(refund));
    }

    // 주문 Id에 대한 삭제
    @Transactional
    public void deleteRefund(long orderId) {
        Optional<Refund> optionalRefund = refundRepository.findByOrder_Id(orderId);
        if (!optionalRefund.isPresent()) {
            throw new RefundNotFoundException(orderId);
        }

        refundRepository.deleteById(orderId);
    }
}
