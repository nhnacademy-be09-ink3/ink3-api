package shop.ink3.api.coupon.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.BookInfo;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.CategoryInfo;
import shop.ink3.api.coupon.coupon.dto.CouponUpdateRequest;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.coupon.service.Impl.CouponServiceImpl;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;
import shop.ink3.api.coupon.store.repository.CouponStoreRepository;

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
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CouponStoreRepository couponStoreRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void createCoupon_success_withoutAssociations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(5);
        CouponCreateRequest req = new CouponCreateRequest(
                1L, "test", now, expires,
                List.of(), List.of()
        );

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(CouponPolicy.builder().id(1L).name("P1").build()));

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer((Answer<Coupon>) invocation -> {
                    Coupon c = invocation.getArgument(0);
                    ReflectionTestUtils.setField(c, "id", 100L);
                    return c;
                });

        CouponResponse resp = couponService.createCoupon(req);

        assertNotNull(resp);
        assertEquals(100L, resp.couponId());
        assertEquals("test", resp.name());
        assertEquals(1L, resp.policyId());
        assertEquals("P1", resp.policyName());
        assertEquals(now, resp.issuableFrom());
        assertEquals(expires, resp.expiresAt());
        assertTrue(resp.books().isEmpty());
        assertTrue(resp.categories().isEmpty());

        verify(policyRepository).findById(1L);
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void createCoupon_noPolicy_throws() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        CouponCreateRequest req = new CouponCreateRequest(
                99L, "no-policy",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                List.of(), List.of()
        );

        assertThrows(PolicyNotFoundException.class,
                () -> couponService.createCoupon(req));
        verify(couponRepository, never()).save(any());
    }

    @Test
    void getCouponById_success() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").build();
        Coupon coupon = Coupon.builder()
                .id(7L)
                .couponPolicy(policy)
                .name("test")
                .issuableFrom(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .build();

        when(couponRepository.findByIdWithFetch(7L))
                .thenReturn(Optional.of(coupon));

        CouponResponse resp = couponService.getCouponById(7L);

        assertEquals(7L, resp.couponId());
        assertEquals("test", resp.name());
        assertEquals(1L, resp.policyId());
        assertEquals("P1", resp.policyName());
    }

    @Test
    void getCouponById_notFound_throws() {
        when(couponRepository.findByIdWithFetch(5L))
                .thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponById(5L));
    }

    @Test
    void getAllCoupons_success() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").build();
        Coupon c1 = Coupon.builder().id(11L).couponPolicy(policy).build();
        Coupon c2 = Coupon.builder().id(22L).couponPolicy(policy).build();

        Pageable unpaged = Pageable.unpaged();
        when(couponRepository.findAllWithAssociations(unpaged))
                .thenReturn(new PageImpl<>(List.of(c1, c2)));

        PageResponse<CouponResponse> page = couponService.getAllCoupons(unpaged);
        List<CouponResponse> list = page.content();

        assertEquals(2, list.size());
        assertEquals(11L, list.get(0).couponId());
        assertEquals(22L, list.get(1).couponId());
    }

    @Test
    void getCouponsByBookId_success() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(5);
        BookCoupon bc = mock(BookCoupon.class);
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(10L);
        when(book.getTitle()).thenReturn("Java");

        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").build();
        Coupon coupon = Coupon.builder().id(99L).couponPolicy(policy).name("B1").expiresAt(expires).build();
        when(bc.getBook()).thenReturn(book);
        when(bc.getCoupon()).thenReturn(coupon);
        when(bc.getId()).thenReturn(5L);

        Pageable unpaged = Pageable.unpaged();
        when(bookCouponRepository.findAllByBookId(10L, unpaged))
                .thenReturn(new PageImpl<>(List.of(bc)));

        PageResponse<CouponResponse> page =
                couponService.getCouponsByBookId(10L, unpaged);
        CouponResponse resp = page.content().getFirst();
        BookInfo info = resp.books().getFirst();

        assertEquals(5L, info.originId());
        assertEquals(10L, info.id());
        assertEquals("Java", info.title());
        assertEquals("BOOK", info.originType());
    }

    @Test
    void getCouponsByBookId_empty_throws() {
        Pageable unpaged = Pageable.unpaged();
        when(bookCouponRepository.findAllByBookId(123L, unpaged))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponsByBookId(123L, unpaged));
    }

    @Test
    void getCouponsByCategoryId_success() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(5);
        CategoryCoupon cc = mock(CategoryCoupon.class);
        Category cat = mock(Category.class);
        when(cat.getId()).thenReturn(8L);
        when(cat.getName()).thenReturn("Fiction");

        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").build();
        Coupon coupon = Coupon.builder().id(55L).couponPolicy(policy).name("C1").expiresAt(expires).build();

        when(cc.getCategory()).thenReturn(cat);
        when(cc.getCoupon()).thenReturn(coupon);
        when(cc.getId()).thenReturn(6L);

        Pageable unpaged = Pageable.unpaged();
        when(categoryCouponRepository.findAllByCategoryId(8L, unpaged))
                .thenReturn(new PageImpl<>(List.of(cc)));

        PageResponse<CouponResponse> page =
                couponService.getCouponsByCategoryId(8L, unpaged);
        CategoryInfo info = page.content().getFirst().categories().getFirst();

        assertEquals(6L, info.originId());
        assertEquals(8L, info.id());
        assertEquals("Fiction", info.name());
        assertEquals("CATEGORY", info.originType());
    }

    @Test
    void getCouponsByCategoryId_empty_throws() {
        Pageable unpaged = Pageable.unpaged();
        when(categoryCouponRepository.findAllByCategoryId(7L, unpaged))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponsByCategoryId(7L, unpaged));
    }

    @Test
    @DisplayName("updateCoupon - 성공 케이스")
    void updateCoupon_success() {
        Long couponId = 5L;
        LocalDateTime now = LocalDateTime.of(2025,5,31,0,0);
        LocalDateTime newExpires = now.plusDays(10);

        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).name("P1").build())
                .name("old-name")
                .issuableFrom(now)
                .expiresAt(now.plusDays(5))
                .createdAt(now)
                .bookCoupons(new HashSet<>())
                .categoryCoupons(new HashSet<>())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));

        when(policyRepository.findById(2L))
                .thenReturn(Optional.of(CouponPolicy.builder().id(2L).name("P2").build()));

        List<Long> bookIds = List.of(101L,102L);
        List<Long> catIds = List.of(201L);

        Book b1 = mock(Book.class);
        when(b1.getId()).thenReturn(101L);
        when(b1.getTitle()).thenReturn("Java Programming");

        Book b2 = mock(Book.class);
        when(b2.getId()).thenReturn(102L);
        when(b2.getTitle()).thenReturn("Spring Boot");

        when(bookRepository.findAllById(bookIds))
                .thenReturn(List.of(b1,b2));

        Category cat = mock(Category.class);
        when(cat.getId()).thenReturn(201L);
        when(cat.getName()).thenReturn("Fiction");
        when(categoryRepository.findAllById(catIds))
                .thenReturn(List.of(cat));

        when(couponRepository.save(existing)).thenReturn(existing);

        CouponUpdateRequest req = new CouponUpdateRequest(
                2L, "new-name", now, newExpires,
                bookIds, catIds
        );

        CouponResponse resp = couponService.updateCoupon(couponId, req);

        assertEquals(couponId, resp.couponId());
        assertEquals("new-name", resp.name());
        assertEquals(2L, resp.policyId());
        assertEquals("P2", resp.policyName());
        assertEquals(now, resp.issuableFrom());
        assertEquals(newExpires, resp.expiresAt());

        assertTrue(resp.books().stream()
                .anyMatch(bi -> bi.id().equals(101L) && bi.title().equals("Java Programming")));
        assertTrue(resp.books().stream()
                .anyMatch(bi -> bi.id().equals(102L) && bi.title().equals("Spring Boot")));

        assertEquals(1, resp.categories().size());
        assertEquals(201L, resp.categories().get(0).id());
        assertEquals("Fiction", resp.categories().get(0).name());
    }

    @Test
    @DisplayName("updateCoupon - CouponNotFoundException 발생")
    void updateCoupon_notFound() {
        Long couponId = 99L;
        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.empty());

        CouponUpdateRequest req = new CouponUpdateRequest(
                1L, "irrelevant",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                Collections.emptyList(), Collections.emptyList()
        );

        assertThrows(CouponNotFoundException.class,
                () -> couponService.updateCoupon(couponId, req));
    }

    @Test
    @DisplayName("updateCoupon - PolicyNotFoundException 발생")
    void updateCoupon_policyNotFound() {
        Long couponId = 7L;
        LocalDateTime now = LocalDateTime.now();
        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));
        when(policyRepository.findById(2L)).thenReturn(Optional.empty());

        CouponUpdateRequest req = new CouponUpdateRequest(
                2L, "name", now, now.plusDays(2),
                Collections.emptyList(), Collections.emptyList()
        );

        assertThrows(PolicyNotFoundException.class,
                () -> couponService.updateCoupon(couponId, req));
    }

    @Test
    @DisplayName("updateCoupon - invalid Book ID 예외")
    void updateCoupon_invalidBookId() {
        Long couponId = 3L;
        LocalDateTime now = LocalDateTime.now();
        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));
        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(CouponPolicy.builder().id(1L).build()));
        List<Long> badBooks = List.of(999L);
        when(bookRepository.findAllById(badBooks)).thenReturn(Collections.emptyList());

        CouponUpdateRequest req = new CouponUpdateRequest(
                1L, "name", now, now.plusDays(2),
                badBooks, Collections.emptyList()
        );

        assertThrows(IllegalArgumentException.class,
                () -> couponService.updateCoupon(couponId, req));
    }

    @Test
    @DisplayName("updateCoupon - invalid Category ID 예외")
    void updateCoupon_invalidCategoryId() {
        Long couponId = 4L;
        LocalDateTime now = LocalDateTime.now();
        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));
        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(CouponPolicy.builder().id(1L).build()));

        List<Long> badCats = List.of(888L);
        when(categoryRepository.findAllById(badCats)).thenReturn(Collections.emptyList());

        CouponUpdateRequest req = new CouponUpdateRequest(
                1L, "name", now, now.plusDays(2),
                Collections.emptyList(), badCats
        );

        assertThrows(IllegalArgumentException.class,
                () -> couponService.updateCoupon(couponId, req));
    }

    @Test
    void deleteCouponById_delegatesToRepository() {
        couponService.deleteCouponById(100L);
        verify(couponRepository).deleteById(100L);
    }
}
