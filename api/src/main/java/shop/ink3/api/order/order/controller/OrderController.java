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
import shop.ink3.api.order.order.dto.OrderDateRequest;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.dto.OrderStatusRequest;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.dto.OrderUpdateRequest;
import shop.ink3.api.order.order.service.OrderMainService;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.order.order.dto.OrderFormCreateRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final String HEADER_USER_ID = "X-User-Id";
    private final OrderService orderService;
    private final OrderMainService orderMainService;

    // 특정 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<OrderResponse>> getOrder(
            @PathVariable long orderId) {
        return ResponseEntity.ok(CommonResponse.success(orderService.getOrder(orderId)));
    }

    // 사용자의 주문목록 조회
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByUser(
            HttpServletRequest request, Pageable pageable) {
        long userId = Long.parseLong(request.getHeader(HEADER_USER_ID));
        return ResponseEntity.ok(
                CommonResponse.success(orderService.getOrderListByUser(userId, pageable)));
    }

    // 특정 기간 사용자 주문목록 조회
    @GetMapping("/me/date")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByUserAndDate(
            HttpServletRequest request,
            @RequestBody OrderDateRequest dateRequest,
            Pageable pageable) {
        long userId = Long.parseLong(request.getHeader(HEADER_USER_ID));
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByUserAndDate(userId, dateRequest, pageable)));
    }

    // 주문 상태 별 사용자 주문목록 조회
    @GetMapping("/me/status")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByUserAndStatus(
            HttpServletRequest request,
            @RequestBody OrderStatusRequest statusRequest,
            Pageable pageable) {
        long userId = Long.parseLong(request.getHeader(HEADER_USER_ID));
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByUserAndStatus(userId, statusRequest, pageable)));
    }

    // 특정 기간 내 전체 주문목록 리스트
    @GetMapping("/date")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByDate(
            @RequestBody OrderDateRequest dateRequest,
            Pageable pageable) {
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByDate(dateRequest, pageable)));
    }

    // 포인트 내역에 대한 주문 조회
    @GetMapping("/point-histories/{pointHistoryId}")
    public ResponseEntity<CommonResponse<OrderResponse>> getOrderByPointHistoryId(@PathVariable long pointHistoryId) {
        return ResponseEntity.ok(
                CommonResponse.success(orderService.getOrderByPointHistoryId(pointHistoryId)));
    }

    // 주문 상태 별 전체 주문목록 조회
    @PostMapping("/status")
    public ResponseEntity<CommonResponse<PageResponse<OrderResponse>>> getOrderListByStatus(
            @RequestBody OrderStatusRequest statusRequest,
            Pageable pageable) {
        return ResponseEntity.ok(
                CommonResponse.success(
                        orderService.getOrderListByStatus(statusRequest, pageable)));
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<CommonResponse<OrderResponse>> createOrder(
            @RequestBody OrderFormCreateRequest orderFormCreateRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(orderMainService.createOrderForm(orderFormCreateRequest)));
    }

    // 주문 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<CommonResponse<OrderResponse>> updateOrder(
            @PathVariable long orderId,
            @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(orderService.updateOrder(orderId, request)));
    }

    // 주문 상태 수정
    @PatchMapping("/{orderId}/order-status")
    public ResponseEntity<Void> setOrderStatus(
            @PathVariable long orderId,
            @RequestBody OrderStatusUpdateRequest request) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.noContent().build();
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
