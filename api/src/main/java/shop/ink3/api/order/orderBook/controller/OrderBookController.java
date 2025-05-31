package shop.ink3.api.order.orderBook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.orderBook.dto.OrderBookResponse;
import shop.ink3.api.order.orderBook.dto.OrderBookUpdateRequest;
import shop.ink3.api.order.orderBook.service.OrderBookService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order-books")
public class OrderBookController {
    private final OrderBookService orderBookService;

    // 주문도서 조회
    @GetMapping("/{orderBookId}")
    public ResponseEntity<CommonResponse<OrderBookResponse>> getOrderBooks(@PathVariable long orderBookId){
        return ResponseEntity
                .ok(CommonResponse.success(orderBookService.getOrderBook(orderBookId)));
    }

    // 주문에 대한 주문도서 리스트 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<CommonResponse<PageResponse<OrderBookResponse>>> getOrderBooks(
            @PathVariable long orderId,Pageable pageable){
        return ResponseEntity
                .ok(CommonResponse.success(orderBookService.getOrderBookListByOrderId(orderId, pageable)));
    }

    @PutMapping("/{orderBookId}")
    public ResponseEntity<CommonResponse<OrderBookResponse>> updateOrderBookResponse(
            @PathVariable long orderBookId,
            @RequestBody OrderBookUpdateRequest request){
        return ResponseEntity
                .ok(CommonResponse.update(orderBookService.updateOrderBook(orderBookId, request)));
    }

    // 주문도서 삭제
    @DeleteMapping("/{orderBookId}")
    public ResponseEntity<CommonResponse<Void>> deleteOrderBook(@PathVariable long orderBookId){
        orderBookService.deleteOrderBook(orderBookId);
        return ResponseEntity.noContent().build();
    }


    // 특정 주문에 대한 주문-도서 정보 삭제
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<CommonResponse<Void>> deleteOrderBooksByOrderId(@PathVariable long orderId){
        orderBookService.deleteOrderBookListByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }
}
