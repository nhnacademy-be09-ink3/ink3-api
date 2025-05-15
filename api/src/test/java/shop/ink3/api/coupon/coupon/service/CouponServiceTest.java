package shop.ink3.api.coupon.coupon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.bookCoupon.entity.dto.BookCouponCreateRequest;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.dto.CategoryCouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.coupon.service.Impl.CouponServiceImpl;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private BookCouponRepository bookCouponRepository;

    @Mock
    private CategoryCouponRepository categoryCouponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void createCoupon_성공() {
        // given
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("10% 할인")
                .build();

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Book book = Book.builder()
                .id(101L)
                .title("테스트 책")
                .build();

        Category category = Category.builder()
                .id(5L)
                .name("테스트 카테고리")
                .build();

        BookCouponCreateRequest bookReq = new BookCouponCreateRequest(book);
        CategoryCouponCreateRequest catReq = new CategoryCouponCreateRequest(category);

        CouponCreateRequest request = new CouponCreateRequest(
                1L,
                "TEST_COUPON",
                TriggerType.BOOK,
                IssueType.DOWNLOAD,
                null,
                LocalDateTime.now().plusDays(7),
                List.of(bookReq),
                List.of(catReq)
        );

        // 쿠폰 저장 시 ID 설정
        when(couponRepository.save(any())).thenAnswer(invocation -> {
            Coupon c = invocation.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 100L);
            return c;
        });

        // when
        CouponResponse result = couponService.createCoupon(request);

        // then
        assertNotNull(result);
        assertEquals("TEST_COUPON", result.couponName());
        assertEquals(1, result.books().size());
        assertEquals("테스트 책", result.books().getFirst().bookName());
        assertEquals(1, result.categories().size());
        assertEquals("테스트 카테고리", result.categories().getFirst().categoryName());

        verify(couponRepository).save(any());
        verify(bookCouponRepository).save(any());
        verify(categoryCouponRepository).save(any());
    }

    @Test
    void createCoupon_정책없음_예외발생() {
        // given
        when(policyRepository.findById(anyLong())).thenReturn(Optional.empty());

        CouponCreateRequest request = new CouponCreateRequest(
                2L,
                "TEST_COUPON",
                TriggerType.WELCOME,
                IssueType.DOWNLOAD,
                null,
                LocalDateTime.now().plusDays(5),
                null,
                null
        );

        // when & then
        assertThrows(PolicyNotFoundException.class, () -> couponService.createCoupon(request));
    }
}

