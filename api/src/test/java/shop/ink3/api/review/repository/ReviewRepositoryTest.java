package shop.ink3.api.review.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Builder;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.review.entity.Review;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @PersistenceContext
    private EntityManager em;

    private User user;
    private Book book;
    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        Publisher publisher = Publisher.builder()
            .name("출판사1")
            .build();
        em.persist(publisher);

        book = Book.builder()
            .ISBN("1234567890123")
            .title("책 제목")
            .contents("내용 요약")
            .description("설명")
            .publishedAt(LocalDate.of(2024, 1, 1))
            .originalPrice(20000)
            .salePrice(18000)
            .discountRate(10)
            .quantity(100)
            .status(BookStatus.AVAILABLE)
            .isPackable(true)
            .thumbnailUrl("url")
            .publisher(publisher)
            .build();
        em.persist(book);

        Membership membership = Membership.builder()
            .name("멤버쉽1")
            .conditionAmount(10000)
            .pointRate(5)
            .isActive(true)
            .isDefault(true)
            .createdAt(LocalDateTime.now())
            .build();
        em.persist(membership);

        user = User.builder()
            .loginId("test")
            .password("test")
            .name("홍길동")
            .email("test@test.com")
            .phone("010-1234-5678")
            .birthday(LocalDate.of(1990, 1, 1))
            .status(UserStatus.ACTIVE)
            .point(1000)
            .lastLoginAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .membership(membership)
            .build();
        em.persist(user);

        orderBook = OrderBook.builder()
            .book(book)
            .price(20000)
            .quantity(1)
            .build();
        em.persist(orderBook);

        Review review = new Review(user, orderBook, "좋아요", "추천합니다", 5);
        em.persist(review);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("단일 리뷰 조회")
    void findByUserId() {
        Optional<Review> result = Optional.ofNullable(reviewRepository.findByUserId(user.getId()));
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("좋아요");
    }

    @Test
    @DisplayName("한 도서에 대한 모든 리뷰 조회")
    void findAllByOrderBook_BookId() {
        Page<Review> page = reviewRepository.findAllByOrderBook_BookId(
            PageRequest.of(0, 10), book.getId()
        );
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getRating()).isEqualTo(5);
    }
}

