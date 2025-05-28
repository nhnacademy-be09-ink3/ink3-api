package shop.ink3.api.order.orderBook.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.exception.BookNotFoundException;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.exception.CouponStoreNotFoundException;
import shop.ink3.api.coupon.store.repository.UserCouponRepository;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.InsufficientBookStockException;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.orderBook.dto.OrderBookCreateRequest;
import shop.ink3.api.order.orderBook.dto.OrderBookResponse;
import shop.ink3.api.order.orderBook.dto.OrderBookUpdateRequest;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.orderBook.exception.OrderBookNotFoundException;
import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
import shop.ink3.api.order.packaging.entity.Packaging;
import shop.ink3.api.order.packaging.exception.PackagingNotFoundException;
import shop.ink3.api.order.packaging.repository.PackagingRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderBookService {
    private final OrderBookRepository orderBookRepository;
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final PackagingRepository packagingRepository;
    private final UserCouponRepository userCouponRepository;

    // 생성
    public void createOrderBook(List<OrderBookCreateRequest> requestList) {
        long orderId = requestList.getFirst().getOrderId();
        for (OrderBookCreateRequest request : requestList) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            Book book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new BookNotFoundException(request.getBookId()));
            Packaging packaging = (Objects.isNull(request.getPackagingId())) ? null
                    : packagingRepository.findById(request.getPackagingId())
                            .orElseThrow(() -> new PackagingNotFoundException(request.getPackagingId()));

            if (book.getQuantity() < request.getQuantity()) {
                throw new InsufficientBookStockException(book.getTitle(), request.getQuantity(), book.getQuantity());
            }
            book.decreaseQuantity(request.getQuantity());
            bookRepository.save(book);

            CouponStore couponStore = null;
            if(request.getCouponStoreId() != null) {
                couponStore = userCouponRepository.findById(request.getCouponStoreId())
                        .orElseThrow(() -> new CouponStoreNotFoundException("해당 쿠폰을 찾지 못했습니다."));
            }

            OrderBook orderBook = OrderBook.builder()
                    .order(order)
                    .book(book)
                    .packaging(packaging)
                    .couponStore(couponStore)
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            orderBookRepository.save(orderBook);
        }
    }

    // 조회
    @Transactional(readOnly = true)
    public OrderBookResponse getOrderBook(long orderBookId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        return OrderBookResponse.from(orderBook);
    }

    // 주문에 대한 도서 리스트 조회 (주문내역 시 첫페이지의 도서 내역만 보여주고 나머지는 ... 외 X권 이렇게 사용)
    @Transactional(readOnly = true)
    public PageResponse<OrderBookResponse> getOrderBookListByOrderId(long orderId, Pageable pageable) {
        Page<OrderBook> page = orderBookRepository.findAllByOrderId(orderId, pageable);
        Page<OrderBookResponse> pageResponse = page.map(OrderBookResponse::from);
        return PageResponse.from(pageResponse);
    }

    // 수정
    public OrderBookResponse updateOrderBook(long orderBookId, OrderBookUpdateRequest request) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        Packaging packaging = (Objects.isNull(request.getPackagingId())) ? null
                : packagingRepository.findById(request.getPackagingId())
                        .orElseThrow(() -> new PackagingNotFoundException(request.getPackagingId()));
        CouponStore couponStore = userCouponRepository.findById(request.getCouponStoreId())
                .orElseThrow(() -> new CouponStoreNotFoundException("해당 쿠폰을 찾지 못했습니다."));
        orderBook.update(request, packaging, couponStore);
        return OrderBookResponse.from(orderBookRepository.save(orderBook));
    }

    // 삭제 특정 주문 도서만 삭제
    public void deleteOrderBook(long orderBookId) {
        orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        orderBookRepository.deleteById(orderBookId);
    }

    // 삭제 orderId에 대한 삭제
    public void deleteOrderBookListByOrderId(long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        orderBookRepository.deleteOrderBookListByOrderId(orderId);
    }

    // 주문 취소에 따른 도서 재고 원상복구
    public void resetBookQuantity(long orderId){
        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        List<OrderBook> orderBooks = orderBookRepository.findAllByOrderId(orderId);
        for(OrderBook orderBook : orderBooks){
            Integer requestQuantity = orderBook.getQuantity();
            Long bookId = orderBook.getBook().getId();
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));
            book.increaseQuantity(requestQuantity);
            bookRepository.save(book);
        }
    }
}
