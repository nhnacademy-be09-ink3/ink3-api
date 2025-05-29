package shop.ink3.api.coupon.coupon.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.external.aladin.client.AladinClientImpl;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.book.publisher.repository.PublisherRepository;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;

    @SpringBootTest
    @Transactional
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)

    public class CouponServiceTest {

        @Autowired
        CouponRepository couponRepository;

        @Autowired
        BookRepository bookRepository;

        @Autowired
        CategoryRepository categoryRepository;

        @Autowired
        PublisherRepository publisherRepository;

        @Autowired
        EntityManager em;  // SQL 쿼리 로그 확인을 위해 flush/clear

        @MockBean
        private AladinClientImpl aladinClient;

        @MockBean
        private RabbitTemplate rabbitTemplate;

        @Test
        void NPlusOne_문제확인_도서_카테고리() {
            // Given
            Publisher publisher = publisherRepository.save(
                    Publisher.builder().name("테스트 출판사").build()
            );

            Book book1 = Book.builder()
                    .isbn("9781234567890")
                    .title("자바의 정석")
                    .contents("기초부터 실무까지 배우는 자바 프로그래밍")
                    .description("자바를 처음 배우는 개발자를 위한 최고의 입문서입니다.")
                    .publisher(publisher) // Publisher 객체 필요
                    .publishedAt(LocalDate.of(2024, 1, 1))
                    .originalPrice(30000)
                    .salePrice(27000)
                    .quantity(100)
                    .status(BookStatus.AVAILABLE) // 예: BookStatus enum 값
                    .isPackable(true)
                    .thumbnailUrl("https://example.com/thumbnail.jpg")
                    .build();
            Book book2 = Book.builder()
                    .isbn("97812345890")
                    .title("자바의 정석2")
                    .contents("기초부터 실무까지 배우는 자바 프로그래밍")
                    .description("자바를 처음 배우는 개발자를 위한 최고의 입문서입니다.")
                    .publisher(publisher) // Publisher 객체 필요
                    .publishedAt(LocalDate.of(2024, 1, 1))
                    .originalPrice(30000)
                    .salePrice(27000)
                    .quantity(100)
                    .status(BookStatus.AVAILABLE) // 예: BookStatus enum 값
                    .isPackable(true)
                    .thumbnailUrl("https://example.com/thumbnail.jpg")
                    .build();

            bookRepository.save(book1);
            bookRepository.save(book2);


            Category parentCategory = categoryRepository.save(
                    Category.builder().name("컴퓨터/IT").build()
            );

            Category category = categoryRepository.save(
                    Category.builder().name("프로그래밍").build()
            );

            Category subCategory = categoryRepository.save(
                    Category.builder()
                            .name("자바")
                            .parent(parentCategory)
                            .build()
            );


            Coupon coupon = Coupon.builder()
                    .name("N+1 테스트 쿠폰")
                    .issueType(IssueType.DOWNLOAD)
                    .couponPolicy(null) // 간단하게 null로 생략
                    .issuableFrom(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusDays(10))
                    .build();

            coupon.addBookCoupon(List.of(book1, book2));
            coupon.addCategoryCoupon(List.of(category, subCategory));

            couponRepository.save(coupon);
            em.flush(); // DB 반영
            em.clear(); // 1차 캐시 제거 → 실제 DB 접근 여부 확인

            // When
            Coupon fetched = couponRepository.findByIdWithFetch(coupon.getId()).orElseThrow();

            // 여기서 N+1 발생 가능
            List<String> bookTitles = fetched.getBookCoupons().stream()
                    .map(bc -> bc.getBook().getTitle())
                    .toList();

            List<String> categoryNames = fetched.getCategoryCoupons().stream()
                    .map(cc -> cc.getCategory().getName())
                    .toList();

            // Then
            System.out.println("도서 타이틀 = " + bookTitles);
            System.out.println("카테고리 이름 = " + categoryNames);

            // 테스트는 쿼리 로그를 콘솔에서 확인하면 OK
        }
    }

