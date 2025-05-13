package shop.ink3.api.order.order.service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.order.dto.OrderCreateRequest;
import shop.ink3.api.order.order.dto.OrderDateRequest;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.dto.OrderStatusRequest;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.dto.OrderUpdateRequest;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;


@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    // 주문Id에 대한 조회 (사용자)
    public OrderResponse getOrder(long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderResponse.from(order);
    }

    // 사용자의 주문 리스트 조회 (사용자)
    public PageResponse<OrderResponse> getOrderListByUser(long userId, Pageable pageable) {
        Page<Order> page = orderRepository.findByUser_Id(userId, pageable);
        Page<OrderResponse> pageResponse = page.map(order -> OrderResponse.from(order));
        return PageResponse.from(pageResponse);
    }

    // 사용자 + 상태별 주문 조회 (사용자)
    public PageResponse<OrderResponse> getOrderListByUserAndStatus(long userId, OrderStatusRequest request, Pageable pageable) {
        Page<Order> page = orderRepository.findByUser_IdAndStatus(userId, request.getOrderStatus(), pageable);
        Page<OrderResponse> pageResponse = page.map(order -> OrderResponse.from(order));
        return PageResponse.from(pageResponse);
    }

    // 기간 별 주문 리스트 조회 (사용자)
    public PageResponse<OrderResponse> getOrderListByUserAndDate(long userId, OrderDateRequest request ,Pageable pageable){
        Page<Order> page = orderRepository.findByUser_IdAndOrderDateBetween(userId, request.getStartDate(), request.getEndDate(),pageable);
        Page<OrderResponse> pageResponse = page.map(order -> OrderResponse.from(order));
        return PageResponse.from(pageResponse);
    }

    // 기간 별 주문 리스트 조회 (관리자)
    public PageResponse<OrderResponse> getOrderListByDate(OrderDateRequest request, Pageable pageable){
        Page<Order> page = orderRepository.findByOrderDateBetween(request.getStartDate(), request.getEndDate() ,pageable);
        Page<OrderResponse> pageResponse = page.map(order -> OrderResponse.from(order));
        return PageResponse.from(pageResponse);
    }


    // 전체 주문 리스트 조회 (관리자)
    public PageResponse<OrderResponse> getOrderList(Pageable pageable){
        Page<Order> page = orderRepository.findAll(pageable);
        Page<OrderResponse> pageResponse = page.map(order -> OrderResponse.from(order));
        return PageResponse.from(pageResponse);
    }

    // 상태별 주문 리스트 조회 (관리자)
    public PageResponse<OrderResponse> getOrderListByStatus( OrderStatusRequest request, Pageable pageable){
        Page<Order> page = orderRepository.findByStatus(request.getOrderStatus(),pageable);
        Page<OrderResponse> pageResponse = page.map(order -> OrderResponse.from(order));
        return PageResponse.from(pageResponse);
    }

    // 생성
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request){
        Order order = Order.builder()
                .id(0L)
                .status(OrderStatus.CONFIRMED)
                .orderedAt(LocalDateTime.now())
                .ordererName(request.getOrdererName())
                .ordererPhone(request.getOrdererPhone())
                .build();
        return OrderResponse.from(orderRepository.save(order));
    }

    // 수정
    @Transactional
    public OrderResponse updateOrder(long orderId,OrderUpdateRequest request){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.update(request);
        return OrderResponse.from(orderRepository.save(order));
    }

    // 삭제
    @Transactional
    public void deleteOrder(long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        orderRepository.deleteById(orderId);
    }

    // 주문 상태 변경 (관리자에 의한)
    public OrderResponse updateOrderStatus(long orderId, OrderStatusUpdateRequest request){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.updateStatus(request.getOrderStatus());
        return OrderResponse.from(order);
    }


}
