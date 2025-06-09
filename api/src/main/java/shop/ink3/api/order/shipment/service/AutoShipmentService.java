package shop.ink3.api.order.shipment.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.order.shipment.dto.ShipmentResponse;

@RequiredArgsConstructor
@Service
public class AutoShipmentService {
    private final ShipmentService shipmentService;
    private final OrderService orderService;

    @Scheduled(cron = "0 1 * * * *")
    public void autoBatchToSHIPPING() {
        List<ShipmentResponse> shipmentByOrderStatus = shipmentService.getShipmentByOrderStatus(OrderStatus.CONFIRMED);

        for(ShipmentResponse shipmentResponse : shipmentByOrderStatus) {
            orderService.updateOrderStatus(shipmentResponse.getOrderId(), new OrderStatusUpdateRequest(OrderStatus.SHIPPING));
            shipmentService.updateShipmentDeliveredAt(shipmentResponse.getOrderId(), LocalDateTime.now());
        }
    }

    @Scheduled(cron = "0 2 * * * *")
    public void autoBatchToDELIVERED() {
        List<ShipmentResponse> shipmentByOrderStatus = shipmentService.getShipmentByOrderStatus(OrderStatus.SHIPPING);

        for(ShipmentResponse shipmentResponse : shipmentByOrderStatus) {
            orderService.updateOrderStatus(shipmentResponse.getOrderId(), new OrderStatusUpdateRequest(OrderStatus.DELIVERED));
            shipmentService.updateShipmentDeliveredAt(shipmentResponse.getOrderId(), LocalDateTime.now());
        }
    }
}
