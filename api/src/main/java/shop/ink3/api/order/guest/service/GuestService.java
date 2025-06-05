package shop.ink3.api.order.guest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.guest.dto.GuestCreateRequest;
import shop.ink3.api.order.guest.dto.GuestResponse;
import shop.ink3.api.order.guest.entiity.Guest;
import shop.ink3.api.order.guest.exception.GuestOrderNotFoundException;
import shop.ink3.api.order.guest.repository.GuestOrderRepository;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class GuestService {
    private final GuestOrderRepository guestOrderRepository;
    private final OrderRepository orderRepository;

    // 비회원 생성
    public GuestResponse createGuest(long orderId, GuestCreateRequest request){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        Guest guestOrder = Guest.builder()
                .order(order)
                .email(request.getEmail())
                .build();
        return GuestResponse.from(guestOrderRepository.save(guestOrder));
    }

    // 비회원 조회 (by orderId)
    @Transactional(readOnly = true)
    public GuestResponse getGuestByOrderId(long orderId){
        Guest guest = guestOrderRepository.findByOrderId(orderId)
                .orElseThrow(GuestOrderNotFoundException::new);
        return GuestResponse.from(guest);
    }

    // 비회원 정보 삭제 (by orderId)
    public void deleteGuestOrderByOrderId(long orderId){
        guestOrderRepository.findByOrderId(orderId)
                .orElseThrow(GuestOrderNotFoundException::new);
        guestOrderRepository.deleteByOrderId(orderId);
    }
}
