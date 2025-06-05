package shop.ink3.api.order.guest.service;

import static shop.ink3.api.order.order.service.OrderService.generateOrderUUID;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.guest.dto.GuestOrderCreateRequest;
import shop.ink3.api.order.guest.dto.GuestOrderDetailsResponse;
import shop.ink3.api.order.guest.dto.GuestOrderResponse;
import shop.ink3.api.order.guest.exception.GuestOrderNotFoundException;
import shop.ink3.api.order.guest.repository.GuestOrderRepository;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.repository.OrderRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class GuestOrderService {
    private final GuestOrderRepository guestOrderRepository;
    private final OrderRepository orderRepository;

    // 주문 Id에 대한 비회원 주문 상세 정보 조회
    @Transactional(readOnly = true)
    public GuestOrderDetailsResponse getGuestOrderDetails(long orderId){
        return guestOrderRepository.findByGuestOrderDetails(orderId)
                .orElseThrow(GuestOrderNotFoundException::new);
    }

    // 비회원 주문 생성
    public GuestOrderResponse createGuestOrder(GuestOrderCreateRequest request) {
        Order order = Order.builder()
                .user(null)
                .status(OrderStatus.CREATED)
                .orderedAt(LocalDateTime.now())
                .ordererName(request.getOrdererName())
                .ordererPhone(request.getOrdererPhone())
                .build();

        Order saveOrder = orderRepository.save(order);
        saveOrder.assignOrderUUID(generateOrderUUID(saveOrder.getId()));
        orderRepository.save(saveOrder);
        return GuestOrderResponse.from(saveOrder);
    }
}
