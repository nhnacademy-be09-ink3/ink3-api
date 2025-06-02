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
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.exception.CouponStoreNotFoundException;
import shop.ink3.api.coupon.store.repository.CouponStoreRepository;
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
    private final CouponStoreRepository couponStoreRepository;

    /**
     * Creates and saves one or more OrderBook entities for a given order based on the provided creation requests.
     *
     * For each request, associates the order with a book, optional packaging, and optional coupon store, while updating book stock accordingly.
     *
     * @param orderId the ID of the order to associate with the new OrderBook entities
     * @param requestList the list of requests specifying book, packaging, coupon, price, and quantity for each OrderBook
     *
     * @throws OrderNotFoundException if the specified order does not exist
     * @throws BookNotFoundException if a requested book does not exist
     * @throws PackagingNotFoundException if a specified packaging does not exist
     * @throws CouponStoreNotFoundException if a specified coupon store does not exist
     * @throws InsufficientBookStockException if the requested quantity exceeds available book stock
     */
    public void createOrderBook(long orderId, List<OrderBookCreateRequest> requestList) {
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
                couponStore = couponStoreRepository.findById(request.getCouponStoreId())
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

    /**
     * Updates an existing order book entry with new packaging and coupon information.
     *
     * @param orderBookId the ID of the order book to update
     * @param request the update request containing new packaging and coupon details
     * @return the updated order book as a response DTO
     *
     * @throws OrderBookNotFoundException if the order book does not exist
     * @throws PackagingNotFoundException if the specified packaging does not exist
     * @throws CouponStoreNotFoundException if the specified coupon store does not exist
     */
    public OrderBookResponse updateOrderBook(long orderBookId, OrderBookUpdateRequest request) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(orderBookId));
        Packaging packaging = (Objects.isNull(request.getPackagingId())) ? null
                : packagingRepository.findById(request.getPackagingId())
                        .orElseThrow(() -> new PackagingNotFoundException(request.getPackagingId()));
        CouponStore couponStore = couponStoreRepository.findById(request.getCouponStoreId())
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
