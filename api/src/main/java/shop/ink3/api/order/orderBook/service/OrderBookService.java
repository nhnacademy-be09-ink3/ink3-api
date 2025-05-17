package shop.ink3.api.order.orderBook.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.orderBook.dto.OrderBookCreateRequest;
import shop.ink3.api.order.orderBook.dto.OrderBookListCreateRequest;
import shop.ink3.api.order.orderBook.dto.OrderBookListResponse;
import shop.ink3.api.order.orderBook.dto.OrderBookResponse;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.orderBook.exception.OrderBookNotFoundException;
import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
import shop.ink3.api.order.packaging.entity.Packaging;
import shop.ink3.api.order.packaging.exception.PackagingNotFoundException;
import shop.ink3.api.order.packaging.repository.PackagingRepository;

@RequiredArgsConstructor
@Service
public class OrderBookService {
    private final OrderBookRepository orderBookRepository;
    private final OrderRepository orderRepository;
    private final PackagingRepository packagingRepository;

    // 생성 -> 제품 개수를 조회해서 구매 가능여부 를 확인해줘야함
    public OrderBookListResponse createOrderBook(long orderId, OrderBookListCreateRequest request){
        List<OrderBook> orderBooks = new ArrayList<>();
        for(OrderBookCreateRequest createRequest : request.getOrderBooks()){
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            Packaging packaging = packagingRepository.findById(createRequest.getPackagingId())
                    .orElseThrow(()->new PackagingNotFoundException(createRequest.getPackagingId()));
            // fix : 객체 채워야함.
            Book book = null;
            CouponStore couponStore = null;

            OrderBook orderBook = OrderBook.builder()
                    .order(order)
                    .book(book)
                    .packaging(packaging)
                    .couponStore(couponStore)
                    .price(createRequest.getPrice())
                    .quantity(createRequest.getQuantity())
                    .build();
            OrderBook saveOrderBook = orderBookRepository.save(orderBook);
            orderBooks.add(saveOrderBook);
        }
        return OrderBookListResponse.from(orderId, orderBooks);
    }


    // 조회
    public OrderBookResponse getOrderBook(long orderBookId){
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        return OrderBookResponse.from(orderBook);
    }

    // 주문에 대한 도서 리스트 조회
    public PageResponse<OrderBookResponse> getOrderBookListByOrderId(long orderId, Pageable pageable){
        Page<OrderBook> page = orderBookRepository.findByOrder_Id(orderId, pageable);
        Page<OrderBookResponse> pageResponse = page.map(OrderBookResponse::from);
        return PageResponse.from(pageResponse);
    }

    // 주문 ID, 도서 Id로 주문-도서 정보 조회
    public OrderBookResponse getOrderBookByOrderIdAndBookId(long orderId, long bookId){
        OrderBook orderBook = orderBookRepository.findByOrder_IdAndBook_Id(orderId,bookId)
                .orElseThrow(OrderBookNotFoundException::new);
        return OrderBookResponse.from(orderBook);
    }

    // 수정 주문 도서에 대한 쿠폰 적용 수정
    public OrderBookResponse updateCouponInOrderBook(long orderBookId, long couponId){
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        // 쿠폰 객체 찾아서 넣어야함.
        CouponStore coupon = null;
        orderBook.updateCoupon(coupon);
        return OrderBookResponse.from(orderBookRepository.save(orderBook));
    }

    // 수정 주문 도서에 대한 수량 수정
    public OrderBookResponse updateQuantityInOrderBook(long orderBookId, int quantity){
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        orderBook.updateQuantity(quantity);
        return OrderBookResponse.from(orderBookRepository.save(orderBook));
    }

    // 삭제 특정 주문 도서만 삭제
    public void deleteOrderBook(long orderBookId){
        orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        orderBookRepository.deleteById(orderBookId);
    }

    // 삭제 orderId에 대한 삭제
    public void deleteOrderBookListByOrderId(long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        orderBookRepository.deleteOrderBookListByOrder_Id(orderId);
    }
}
