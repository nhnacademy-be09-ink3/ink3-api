package shop.ink3.api.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.review.common.exception.ReviewNotFoundException;
import shop.ink3.api.review.dto.ReviewRequest;
import shop.ink3.api.review.dto.ReviewResponse;
import shop.ink3.api.review.entity.Review;
import shop.ink3.api.review.repository.ReviewRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Publisher publisher;
    private Book book;
    private OrderBook orderBook1;
    private OrderBook orderBook2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
            .id(1L)
            .loginId("test")
            .name("test")
            .email("test@test.com")
            .phone("010-1234-5678")
            .birthday(LocalDate.of(2025, 1, 1))
            .point(1000)
            .status(UserStatus.ACTIVE)
            .lastLoginAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        publisher = Publisher.builder()
            .id(1L)
            .name("출판사1")
            .build();

        book = Book.builder()
            .id(1L)
            .ISBN("1234567890123")
            .title("예제 책 제목")
            .contents("책 내용 요약")
            .description("책 상세 설명")
            .publishedAt(LocalDate.of(2024, 1, 1))
            .originalPrice(20000)
            .salePrice(18000)
            .discountRate((18000 * 100) / 20000)
            .quantity(100)
            .status(BookStatus.AVAILABLE)
            .isPackable(true)
            .thumbnailUrl("https://example.com/image.jpg")
            .publisher(publisher)
            .build();

        orderBook1 = OrderBook.builder()
            .id(1L)
            .order(null)
            .book(book)
            .packaging(null)
            .couponStore(null)
            .price(20000)
            .quantity(100)
            .build();

        orderBook2 = OrderBook.builder()
            .id(2L)
            .order(null)
            .book(book)
            .packaging(null)
            .couponStore(null)
            .price(20000)
            .quantity(100)
            .build();
    }

    @Test
    @DisplayName("리뷰 등록")
    void addReview() {
        ReviewRequest request = new ReviewRequest(user, orderBook1, "title1", "content1", 5);
        Review review = ReviewRequest.toEntity(request);

        ReflectionTestUtils.setField(review, "id", 1L);

        when(reviewRepository.save(ArgumentMatchers.any(Review.class))).thenReturn(review);

        ReviewResponse response = reviewService.addReview(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("title1");
        assertThat(response.content()).isEqualTo("content1");
    }

    @Test
    @DisplayName("주문 도서의 리뷰 조회")
    void getReviewByUserId() {
        Review review = new Review(user, orderBook1, "title1", "content1", 5);
        ReflectionTestUtils.setField(review, "id", 1L);

        when(reviewRepository.findByUserId(user.getId())).thenReturn(review);

        ReviewResponse response = reviewService.getReviewByUserId(user.getId());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("title1");
        assertThat(response.content()).isEqualTo("content1");
        assertThat(response.rating()).isEqualTo(5);
    }

    @Test
    @DisplayName("한 도서의 리뷰 전체 조회")
    void getReviewsByBookId() {
        List<Review> reviews = new ArrayList<>();
        ReviewRequest reviewRequest1 = new ReviewRequest(user, orderBook1, "title1", "content1", 5);
        Review review1 = ReviewRequest.toEntity(reviewRequest1);
        ReflectionTestUtils.setField(review1, "id", 1L);

        ReviewRequest cartRequest2 = new ReviewRequest(user, orderBook2, "title2", "content2", 4);
        Review review2 = ReviewRequest.toEntity(cartRequest2);
        ReflectionTestUtils.setField(review2, "id", 2L);

        reviews.add(review1);
        reviews.add(review2);

        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), reviews.size());

        when(reviewRepository.findAllByOrderBook_BookId(any(), eq(book.getId()))).thenReturn(reviewPage);

        Page<ReviewResponse> responses = reviewService.getReviewsByBookId(PageRequest.of(0, 10), book.getId());

        assertThat(responses).hasSize(2);
        assertThat(responses.getContent().get(0).id()).isEqualTo(1L);
        assertThat(responses.getContent().get(1).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReviewSuccess() {
        ReviewRequest reviewRequest = new ReviewRequest(user, orderBook1, "title1", "content1", 5);
        Review review = ReviewRequest.toEntity(reviewRequest);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(ArgumentMatchers.any(Review.class))).thenReturn(review);

        reviewService.deleteReview(1L);
    }

    @Test
    @DisplayName("리뷰 삭제 실패")
    void deleteReviewFailure() {
        when(reviewRepository.findById(0L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(0L))
            .isInstanceOf(ReviewNotFoundException.class)
            .hasMessageContaining("존재하지 않는 리뷰입니다.");
    }
}
