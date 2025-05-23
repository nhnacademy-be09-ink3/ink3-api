package shop.ink3.api.order.shipment.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.shipment.dto.ShipmentCreateRequest;
import shop.ink3.api.order.shipment.dto.ShipmentResponse;
import shop.ink3.api.order.shipment.dto.ShipmentUpdateRequest;
import shop.ink3.api.order.shipment.entity.Shipment;
import shop.ink3.api.order.shipment.exception.ShipmentNotFoundException;
import shop.ink3.api.order.shipment.repository.ShipmentRepository;

@RequiredArgsConstructor
@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    // 생성
    @Transactional
    public ShipmentResponse createShipment(ShipmentCreateRequest request) {
        Optional<Order> optionalOrder = orderRepository.findById(request.getOrderId());
        if(optionalOrder.isEmpty()){
            throw new OrderNotFoundException(request.getOrderId());
        }
        Order order = optionalOrder.get();
        Shipment shipment = Shipment.builder()
                .order(order)
                .preferredDeliveryDate(request.getPreferredDeliveryDate())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .postalCode(request.getPostalCode())
                .defaultAddress(request.getDefaultAddress())
                .extraAddress(request.getExtraAddress())
                .detailAddress(request.getDetailAddress())
                .shippingFee(request.getShippingFee())
                .shippingCode(request.getShippingCode())
                .build();
        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    // 특정 주문에 대한 배송 정보 조회
    public ShipmentResponse getShipment(long orderId) {
        Shipment shipment = getShipmentOrThrow(orderId);
        return ShipmentResponse.from(shipment);
    }


    // 특정 사용자의 배송 list
    public PageResponse<ShipmentResponse> getUserShipmentList(long userId, Pageable pageable) {
        Page<Shipment> shipmentpage = shipmentRepository.findByOrder_UserId(userId, pageable);
        Page<ShipmentResponse> shipmentResponsePage = shipmentpage.map(shipment -> ShipmentResponse.from(shipment));
        return PageResponse.from(shipmentResponsePage);
    }

    // 주문 상태에 따른 배송 list
    public PageResponse<ShipmentResponse> getShipmentListByOrderStatus(long userId, OrderStatus status, Pageable pageable){
        Page<Shipment> shipmentPage = shipmentRepository.findByOrder_UserIdAndOrder_Status(userId, status, pageable);
        Page<ShipmentResponse> shipmentResponsePage = shipmentPage.map(shipment -> ShipmentResponse.from(shipment));
        return PageResponse.from(shipmentResponsePage);
    }

    // 수정
    @Transactional
    public ShipmentResponse updateShipment(long orderId, ShipmentUpdateRequest request) {
        Shipment shipment = getShipmentOrThrow(orderId);
        shipment.update(request);
        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    // 삭제
    @Transactional
    public void deleteShipment(long orderId) {
        Shipment shipment = getShipmentOrThrow(orderId);
        shipmentRepository.deleteById(shipment.getId());
    }

    // 배달 완료 시간 변경
    @Transactional
    public ShipmentResponse updateShipmentDeliveredAt(long orderId, LocalDateTime deliveredAt) {
        Shipment shipment = getShipmentOrThrow(orderId);
        shipment.updateDeliveredAt(deliveredAt);
        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    // 조회 로직
    private Shipment getShipmentOrThrow(long orderId) {
        Optional<Shipment> optionalShipping = shipmentRepository.findByOrder_Id(orderId);
        if (optionalShipping.isEmpty()) {
            throw new ShipmentNotFoundException();
        }
        return optionalShipping.get();
    }
}
