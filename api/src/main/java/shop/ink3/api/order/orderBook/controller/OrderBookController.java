package shop.ink3.api.order.orderBook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.order.orderBook.dto.OrderBookListCreateRequest;
import shop.ink3.api.order.orderBook.dto.OrderBookListResponse;
import shop.ink3.api.order.orderBook.service.OrderBookService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orderBooks")
public class OrderBookController {
    private final OrderBookService orderBookService;

    @PostMapping
    public ResponseEntity<CommonResponse<OrderBookListResponse>> createOrderBooks(@RequestBody OrderBookListCreateRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(orderBookService.createOrderBook(request.getOrderId(),request)));
    }

    // 주문도서 조회

    // 주문에 대한 주문도서 리스트 조회

    // 주문도서 수정

    // 주문도서 삭제
}
