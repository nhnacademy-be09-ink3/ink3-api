package shop.ink3.api.order.order.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.order.dto.OrderCreateRequest;
import shop.ink3.api.order.order.dto.OrderDateRequest;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.dto.OrderStatusRequest;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.dto.OrderUpdateRequest;
import shop.ink3.api.order.order.service.OrderService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<OrderResponse>> getOrder(
            @PathVariable long orderId) {
        return ResponseEntity.ok(CommonResponse.success(orderService.getOrder(orderId)));
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByUser(
            HttpServletRequest request, Pageable pageable){
        long userId = Long.parseLong(request.getHeader("X_USER_ID"));
        return ResponseEntity.ok(
                CommonResponse.success(orderService.getOrderListByUser(userId, pageable)));
    }

    @GetMapping("/me/date")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByUserAndDate(
            HttpServletRequest request,
            @RequestBody OrderDateRequest dateRequest,
            Pageable pageable){
        long userId = Long.parseLong(request.getHeader("X_USER_ID"));
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByUserAndDate(userId, dateRequest, pageable)));
    }

    @GetMapping("/me/status")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByUserAndStatus(
            HttpServletRequest request,
            @RequestBody OrderStatusRequest statusRequest,
            Pageable pageable){
        long userId = Long.parseLong(request.getHeader("X_USER_ID"));
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByUserAndStatus(userId, statusRequest, pageable)));
    }


    @GetMapping("/date")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByDate(
            @RequestBody OrderDateRequest dateRequest,
            Pageable pageable){
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByDate(dateRequest, pageable)));
    }

    @PostMapping("/status")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByStatus(
            @RequestBody OrderStatusRequest statusRequest,
            Pageable pageable){
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByStatus(statusRequest, pageable)));
    }


    @PostMapping
    public ResponseEntity<CommonResponse<OrderResponse>> createOrder(
            @RequestBody OrderCreateRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(orderService.createOrder(request)));
    }
    @PutMapping("/{orderId}")
    public ResponseEntity<CommonResponse<OrderResponse>> updateOrder(
            @PathVariable long orderId,
            @RequestBody OrderUpdateRequest request){
        return ResponseEntity.ok(CommonResponse.update(orderService.updateOrder(orderId, request)));
    }


    @PatchMapping("/{orderId}/order-status")
    public ResponseEntity<Void> setOrderStatus(
            @PathVariable long orderId,
            @RequestBody OrderStatusUpdateRequest request){
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable long orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
