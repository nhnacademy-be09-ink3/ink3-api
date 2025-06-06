package shop.ink3.api.coupon.coupon.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void createCoupon_success_withoutAssociations() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(5);
        CouponCreateRequest req = new CouponCreateRequest(
                1L,
                "test",
                now,
                expires,
                now,
                List.of(),
                List.of()
        );
        CouponPolicy policy = CouponPolicy.builder().id(1L).build();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        when(couponRepository.save(any(Coupon.class))).thenAnswer(new Answer<Coupon>() {
            @Override
            public Coupon answer(InvocationOnMock invocation) {
                Coupon arg = invocation.getArgument(0);
                ReflectionTestUtils.setField(arg, "id", 100L);
                return arg;
            }
        });

        // when
        CouponResponse resp = couponService.createCoupon(req);

        // then
        assertNotNull(resp);
        assertEquals(100L, resp.couponId());
        assertEquals(1L, resp.policyId());
        assertEquals("test", resp.name());
        assertEquals(now, resp.issuableFrom());
        assertEquals(expires, resp.expiresAt());
        assertTrue(resp.books().isEmpty());
        assertTrue(resp.categories().isEmpty());

        verify(policyRepository).findById(1L);
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void createCoupon_noPolicy_throws() {
        // given
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());
        CouponCreateRequest req = new CouponCreateRequest(
                99L,
                "no-policy",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                List.of(),
                List.of()
        );

        // when / then
        assertThrows(PolicyNotFoundException.class,
                () -> couponService.createCoupon(req)
        );
        verify(couponRepository, never()).save(any());
    }

    // --- getCouponById ---

    @Test
    void getCouponById_success() {
        var coupon = Coupon.builder()
                .id(7L)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .name("test")
                .issuableFrom(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .build();

        when(couponRepository.findByIdWithFetch(7L))
                .thenReturn(Optional.of(coupon));

        var resp = couponService.getCouponById(7L);

        assertEquals(7L, resp.couponId());
        assertEquals("test", resp.name());
    }

    @Test
    void getCouponById_notFound_throws() {
        when(couponRepository.findByIdWithFetch(5L))
                .thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponById(5L));
    }

    // --- getAllCoupons ---

    @Test
    void getAllCoupons_success() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).build();
        var c1 = Coupon.builder().id(11L).couponPolicy(policy).build();
        var c2 = Coupon.builder().id(22L).couponPolicy(policy).build();
        when(couponRepository.findAllWithAssociations(Pageable.unpaged()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(c1, c2)));

        PageResponse<CouponResponse> page = couponService.getAllCoupons(Pageable.unpaged());
        List<CouponResponse> list = page.content();  // assumes PageResponse has getData()

        assertEquals(2, list.size());
        assertEquals(11L, list.get(0).couponId());
        assertEquals(22L, list.get(1).couponId());
    }

    @Test
    void getCouponsByBookId_success() {
        BookCoupon bc = mock(BookCoupon.class);
        Book book = mock(Book.class);
        CouponPolicy policy = CouponPolicy.builder().id(1L).build();
        Coupon coupon = Coupon.builder().id(99L).couponPolicy(policy).name("B1").build();
        when(book.getId()).thenReturn(10L);
        when(book.getTitle()).thenReturn("Java");
        when(bc.getId()).thenReturn(5L);
        when(bc.getBook()).thenReturn(book);
        when(bc.getCoupon()).thenReturn(coupon);

        when(bookCouponRepository.findAllByBookId(10L, Pageable.unpaged()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(bc)));

        PageResponse<CouponResponse> page = couponService.getCouponsByBookId(10L, Pageable.unpaged());
        List<CouponResponse> list = page.content();

        assertEquals(1, list.size());
        var info = list.get(0).books().get(0);
        assertEquals(5L, info.originId());
        assertEquals(10L, info.id());
        assertEquals("Java", info.title());
    }

    @Test
    void getCouponsByBookId_empty_throws() {
        when(bookCouponRepository.findAllByBookId(123L, Pageable.unpaged()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponsByBookId(123L, Pageable.unpaged()));
    }

    // --- getCouponsByCategoryId ---

    @Test
    void getCouponsByCategoryId_success() {
        CategoryCoupon cc = mock(CategoryCoupon.class);
        CouponPolicy policy = CouponPolicy.builder().id(1L).build();
        Coupon coupon = Coupon.builder().id(55L).couponPolicy(policy).name("C1").build();
        Category cat = mock(Category.class);
        when(cat.getId()).thenReturn(8L);
        when(cat.getName()).thenReturn("Fiction");
        when(cc.getId()).thenReturn(6L);
        when(cc.getCategory()).thenReturn(cat);
        when(cc.getCoupon()).thenReturn(coupon);

        when(categoryCouponRepository.findAllByCategoryId(8L, Pageable.unpaged()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(cc)));

        PageResponse<CouponResponse> page = couponService.getCouponsByCategoryId(8L, Pageable.unpaged());
        List<CouponResponse> list = page.content();

        assertEquals(1, list.size());
        var info = list.get(0).categories().get(0);
        assertEquals(6L, info.originId());
        assertEquals(8L, info.id());
        assertEquals("Fiction", info.name());
    }

    @Test
    void getCouponsByCategoryId_empty_throws() {
        when(categoryCouponRepository.findAllByCategoryId(7L, Pageable.unpaged()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponsByCategoryId(7L, Pageable.unpaged()));
    }

    @Test
    @DisplayName("updateCoupon - 성공 케이스")
    void updateCoupon_success() {
        // given
        Long couponId = 5L;
        LocalDateTime now = LocalDateTime.of(2025, 5, 31, 0, 0);
        LocalDateTime newExpires = now.plusDays(10);

        // 1) 기존 Coupon 엔티티: 초기 관계 없이 빈 상태
        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .name("old-name")
                .issuableFrom(now)
                .expiresAt(now.plusDays(5))
                .createdAt(now)
                .bookCoupons(new HashSet<>())
                .categoryCoupons(new HashSet<>())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));

        // 2) 업데이트 요청 객체
        List<Long> bookIds = List.of(101L, 102L);
        List<Long> categoryIds = List.of(201L);
        CouponUpdateRequest req = new CouponUpdateRequest(
                2L,                  // 새로운 정책 ID
                "new-name",          // 새로운 이름
                now,
                newExpires,
                now,
                bookIds,
                categoryIds
        );

        // 3) Policy 조회 모킹
        CouponPolicy newPolicy = CouponPolicy.builder().id(2L).build();
        when(policyRepository.findById(2L)).thenReturn(Optional.of(newPolicy));

        // 4) Book 리스트 조회 모킹
        Book book1 = mock(Book.class);
        when(book1.getId()).thenReturn(101L);
        when(book1.getTitle()).thenReturn("Java Programming");

        Book book2 = mock(Book.class);
        when(book2.getId()).thenReturn(102L);
        when(book2.getTitle()).thenReturn("Spring Boot");

        when(bookRepository.findAllById(bookIds))
                .thenReturn(List.of(book1, book2));

        // 5) Category 리스트 조회 모킹
        Category cat = mock(Category.class);
        when(cat.getId()).thenReturn(201L);
        when(cat.getName()).thenReturn("Fiction");

        when(categoryRepository.findAllById(categoryIds))
                .thenReturn(List.of(cat));

        // 6) save() 호출 시 existing 객체를 그대로 반환
        when(couponRepository.save(existing)).thenReturn(existing);

        // when
        CouponResponse resp = couponService.updateCoupon(couponId, req);

        // then: 필드가 제대로 변경되었는지 검증
        assertEquals(couponId, resp.couponId());
        assertEquals(2L, resp.policyId());
        assertEquals("new-name", resp.name());
        assertEquals(now, resp.issuableFrom());
        assertEquals(newExpires, resp.expiresAt());

        // BookInfo 리스트 검증: 순서 보장을 위해 id 기준으로 검증
        List<BookInfo> books = resp.books();
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(bi -> bi.id().equals(101L) && bi.title().equals("Java Programming")));
        assertTrue(books.stream().anyMatch(bi -> bi.id().equals(102L) && bi.title().equals("Spring Boot")));

        // CategoryInfo 리스트 검증
        List<CategoryInfo> categories = resp.categories();
        assertEquals(1, categories.size());
        assertEquals(201L, categories.get(0).id());
        assertEquals("Fiction", categories.get(0).name());

        // verify 호출된 메서드
        verify(couponRepository).findByIdWithFetch(couponId);
        verify(policyRepository).findById(2L);
        verify(bookRepository).findAllById(bookIds);
        verify(categoryRepository).findAllById(categoryIds);
        verify(couponRepository).save(existing);
    }

    @Test
    @DisplayName("updateCoupon - CouponNotFoundException 발생")
    void updateCoupon_notFound() {
        // given
        Long couponId = 99L;
        CouponUpdateRequest req = new CouponUpdateRequest(
                1L, "irrelevant",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(CouponNotFoundException.class,
                () -> couponService.updateCoupon(couponId, req));

        verify(couponRepository).findByIdWithFetch(couponId);
        verifyNoMoreInteractions(policyRepository, bookRepository, categoryRepository, couponRepository);
    }

    @Test
    @DisplayName("updateCoupon - PolicyNotFoundException 발생")
    void updateCoupon_policyNotFound() {
        // given
        Long couponId = 7L;
        LocalDateTime now = LocalDateTime.now();

        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .name("old-name")
                .issuableFrom(now)
                .expiresAt(now.plusDays(5))
                .createdAt(now)
                .bookCoupons(new HashSet<>())
                .categoryCoupons(new HashSet<>())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));

        // Policy 미존재
        when(policyRepository.findById(2L)).thenReturn(Optional.empty());

        CouponUpdateRequest req = new CouponUpdateRequest(
                2L, "new-name",
                now,
                now.plusDays(2),
                now,
                Collections.emptyList(),
                Collections.emptyList()
        );

        // when / then
        assertThrows(PolicyNotFoundException.class,
                () -> couponService.updateCoupon(couponId, req));

        verify(couponRepository).findByIdWithFetch(couponId);
        verify(policyRepository).findById(2L);
        verifyNoMoreInteractions(bookRepository, categoryRepository, couponRepository);
    }

    @Test
    @DisplayName("updateCoupon - 존재하지 않는 Book ID 포함 시 IllegalArgumentException 발생")
    void updateCoupon_invalidBookId() {
        // given
        Long couponId = 3L;
        LocalDateTime now = LocalDateTime.now();

        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .name("old")
                .issuableFrom(now)
                .expiresAt(now.plusDays(5))
                .createdAt(now)
                .bookCoupons(new HashSet<>())
                .categoryCoupons(new HashSet<>())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));

        CouponPolicy policy = CouponPolicy.builder().id(1L).build();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // bookRepository.findAllById에서 빈 리스트를 반환 (요청한 ID 크기와 불일치)
        List<Long> bookIds = List.of(999L);
        when(bookRepository.findAllById(bookIds)).thenReturn(Collections.emptyList());

        CouponUpdateRequest req = new CouponUpdateRequest(
                1L, "new-name",
                now,
                now.plusDays(2),
                now,
                bookIds,
                Collections.emptyList()
        );

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> couponService.updateCoupon(couponId, req));

        verify(couponRepository).findByIdWithFetch(couponId);
        verify(policyRepository).findById(1L);
        verify(bookRepository).findAllById(bookIds);
        verifyNoMoreInteractions(categoryRepository, couponRepository);
    }

    @Test
    @DisplayName("updateCoupon - 존재하지 않는 Category ID 포함 시 IllegalArgumentException 발생")
    void updateCoupon_invalidCategoryId() {
        Long couponId = 4L;
        LocalDateTime now = LocalDateTime.now();

        Coupon existing = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .name("old")
                .issuableFrom(now)
                .expiresAt(now.plusDays(5))
                .createdAt(now)
                .bookCoupons(new HashSet<>())
                .categoryCoupons(new HashSet<>())
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));

        CouponPolicy policy = CouponPolicy.builder().id(1L).build();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        List<Long> categoryIds = List.of(888L);
        when(categoryRepository.findAllById(categoryIds)).thenReturn(Collections.emptyList());

        CouponUpdateRequest req = new CouponUpdateRequest(
                1L, "new-name",
                now,
                now.plusDays(2),
                now,
                Collections.emptyList(),  // bookIdList
                categoryIds               // categoryIdList
        );

        assertThrows(IllegalArgumentException.class,
                () -> couponService.updateCoupon(couponId, req));

        verify(couponRepository).findByIdWithFetch(couponId);
        verify(policyRepository).findById(1L);
        verify(categoryRepository).findAllById(categoryIds);
        verifyNoMoreInteractions(couponRepository);
    }

    @Test
    void deleteCouponById_delegatesToRepository() {
        couponService.deleteCouponById(100L);
        verify(couponRepository).deleteById(100L);
    }
}
