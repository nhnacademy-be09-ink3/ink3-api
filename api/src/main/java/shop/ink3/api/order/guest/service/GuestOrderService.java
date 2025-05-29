package shop.ink3.api.order.guest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.config.SecurityConfig;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.guest.dto.GuestOrderCreateRequest;
import shop.ink3.api.order.guest.dto.GuestOrderResponse;
import shop.ink3.api.order.guest.entiity.GuestOrder;
import shop.ink3.api.order.guest.exception.GuestOrderNotFoundException;
import shop.ink3.api.order.guest.repository.GuestOrderRepository;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class GuestOrderService {
    private final GuestOrderRepository guestOrderRepository;
    private final OrderRepository orderRepository;
    private final SecurityConfig securityConfig;

    // 생성
    public GuestOrderResponse createGuestOrder(GuestOrderCreateRequest request){
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(OrderNotFoundException::new);
        GuestOrder guestOrder = GuestOrder.builder()
                .order(order)
                .email(request.email())
                .password(securityConfig.passwordEncoder().encode(request.password()))
                .build();
        return GuestOrderResponse.from(guestOrder);
    }

    // 비회원 주문 번호로 주문 조회
    @Transactional(readOnly = true)
    public GuestOrderResponse getGuestOrder(long guestOrderId){
        GuestOrder guestOrder = guestOrderRepository.findById(guestOrderId)
                .orElseThrow(() -> new GuestOrderNotFoundException(guestOrderId));
        return GuestOrderResponse.from(guestOrder);
    }

    // 주문 Id에 대한 비회원 주문 조회
    @Transactional(readOnly = true)
    public GuestOrderResponse getGuestOrderByOrderId(long orderId){
        GuestOrder guestOrder = guestOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return GuestOrderResponse.from(guestOrder);
    }

    // 자신의 이메일과 비밀번호로 주문리스트 조회하기
    @Transactional(readOnly = true)
    public PageResponse<GuestOrderResponse> getGuestOrderList(String email, String password, Pageable pageable){
        Page<GuestOrder> pageGuestOrder = guestOrderRepository.findAllByEmailAndAndPassword(email,
                securityConfig.passwordEncoder().encode(password), pageable);
        Page<GuestOrderResponse> pageGuestOrderResponse = pageGuestOrder.map(GuestOrderResponse::from);
        return PageResponse.from(pageGuestOrderResponse);
    }

    // 삭제 비회원 주문ID로 삭제
    public void deleteGuestOrder(long guestOrderId){
        guestOrderRepository.findById(guestOrderId)
                .orElseThrow(() -> new GuestOrderNotFoundException(guestOrderId));
        guestOrderRepository.deleteById(guestOrderId);
    }

    // 삭제 주문ID로 삭제
    public void deleteGuestOrderByOrderId(long orderId){
        guestOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        guestOrderRepository.deleteByOrderId(orderId);
    }
}
