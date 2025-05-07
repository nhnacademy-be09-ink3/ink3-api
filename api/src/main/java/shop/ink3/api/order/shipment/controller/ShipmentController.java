package shop.ink3.api.order.shipment.controller;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.shipment.dto.ShipmentCreateRequest;
import shop.ink3.api.order.shipment.dto.ShipmentResponse;
import shop.ink3.api.order.shipment.dto.ShipmentUpdateRequest;
import shop.ink3.api.order.shipment.service.ShipmentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<ShipmentResponse>> getShipment(
            @PathVariable long orderId) {
        return ResponseEntity
                .ok(CommonResponse.success(shipmentService.getShipment(orderId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<PageResponse<ShipmentResponse>>> getUserShipment(
            @PathVariable long userId, Pageable pageable) {
        return ResponseEntity
                .ok(CommonResponse.success(shipmentService.getUserShipmentList(userId, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<ShipmentResponse>> createShipment(
            @RequestBody ShipmentCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(shipmentService.createShipment(request)));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<CommonResponse<ShipmentResponse>> updateShipment(
            @PathVariable long orderId,
            @RequestBody ShipmentUpdateRequest request) {
        return ResponseEntity
                .ok(CommonResponse.update(shipmentService.updateShipment(orderId, request)));
    }

    @PatchMapping("/{orderId}/delivered-at")
    public ResponseEntity<CommonResponse<ShipmentResponse>> updateShipmentDeliveredAt(
            @PathVariable long orderId,
            @RequestParam LocalDateTime deliveredAt) {
        return ResponseEntity
                .ok(CommonResponse.update(shipmentService.updateShipmentDeliveredAt(orderId, deliveredAt)));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteShippingPolicy(@PathVariable long orderId) {
        shipmentService.deleteShipment(orderId);
        return ResponseEntity.ok().build();
    }
}
