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
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
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
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.repository.CouponStoreRepository;
import shop.ink3.api.coupon.store.service.CouponStoreService;

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
    @InjectMocks
    private CouponStoreService couponStoreService;
    @Test
    void createCoupon_success_withoutAssociations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(5);
        CouponCreateRequest req = new CouponCreateRequest(
                1L, "test", now, expires,
                List.of(), List.of()
        );

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(CouponPolicy.builder().id(1L).name("P1").discountPercentage(10).discountValue(0).build()));

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
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").discountPercentage(10).discountValue(0).build();
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
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").discountPercentage(10).discountValue(0).build();
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
        LocalDateTime issuableFrom = now.minusDays(1);
        LocalDateTime expires = now.plusDays(5);
        BookCoupon bc = mock(BookCoupon.class);
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(10L);
        when(book.getTitle()).thenReturn("Java");

        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").discountPercentage(10).discountValue(0).build();
        Coupon coupon = Coupon.builder().id(99L).couponPolicy(policy).name("B1").issuableFrom(issuableFrom).expiresAt(expires).isActive(true).build();
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
        LocalDateTime issuableFrom = now.minusDays(1);
        CategoryCoupon cc = mock(CategoryCoupon.class);
        Category cat = mock(Category.class);
        when(cat.getId()).thenReturn(8L);
        when(cat.getName()).thenReturn("Fiction");

        CouponPolicy policy = CouponPolicy.builder().id(1L).name("P1").discountPercentage(10).discountValue(0).build();
        Coupon coupon = Coupon.builder().id(55L).couponPolicy(policy).name("C1").issuableFrom(issuableFrom).expiresAt(expires).isActive(true).build();

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
                .couponPolicy(CouponPolicy.builder().id(1L).name("P1").discountPercentage(10).discountValue(0).build())
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
                .thenReturn(Optional.of(CouponPolicy.builder().id(2L).name("P2").discountPercentage(10).discountValue(0).build()));

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
                2L, "new-name", now, newExpires, true,
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
        assertEquals(201L, resp.categories().getFirst().id());
        assertEquals("Fiction", resp.categories().getFirst().name());
    }

    @Test
    @DisplayName("updateCoupon - CouponNotFoundException 발생")
    void updateCoupon_notFound() {
        Long couponId = 99L;
        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.empty());

        CouponUpdateRequest req = new CouponUpdateRequest(
                1L, "irrelevant",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), true,
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
                2L, "name", now, now.plusDays(2), true,
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
                1L, "name", now, now.plusDays(2), true,
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
                1L, "name", now, now.plusDays(2), true,
                Collections.emptyList(), badCats
        );

        assertThrows(IllegalArgumentException.class,
                () -> couponService.updateCoupon(couponId, req));
    }

    @Test
    @DisplayName("updateCoupon — isActive=false 일 때 CouponStoreService.disable 호출")
    void updateCoupon_whenBecomesInactive_thenDisableStores() {
        Long couponId = 5L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newExpires = now.plusDays(10);

        // 기존 쿠폰 엔티티 준비
        Coupon realCoupon = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .name("old")
                .issuableFrom(now)
                .expiresAt(now.plusDays(5))
                .isActive(true)
                .build();
        Coupon existing = spy(realCoupon);
        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existing));

        // 새 정책 준비
        CouponPolicy newPolicy = CouponPolicy.builder().id(2L).discountPercentage(10).discountValue(0)
                .build();
        when(policyRepository.findById(2L))
                .thenReturn(Optional.of(newPolicy));

        // save 후 isActive=false 반환
        when(couponRepository.save(existing)).thenReturn(existing);
        doReturn(false).when(existing).isActive();

        // 비활성화 요청
        CouponUpdateRequest req = new CouponUpdateRequest(
                2L, "new", now, newExpires,
                false,            // isActive=false!
                List.of(), List.of()
        );

        couponService.updateCoupon(couponId, req);

        // disableCouponStoresByCouponId가 1회 호출되어야 함
        verify(couponStoreService, times(1))
                .disableCouponStoresByCouponId(couponId);
    }

    @Test
    @DisplayName("updateCoupon — isActive=true 일 때 CouponStoreService.disable 호출 안 됨")
    void updateCoupon_whenStaysActive_thenDoNotDisableStores() {
        // given
        Long couponId = 5L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newExpires = now.plusDays(10);

        // 기존 쿠폰 엔티티 준비 (isActive=true)
        Coupon existingCoupon = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(1L).build())
                .name("Active Coupon")
                .issuableFrom(now)
                .expiresAt(now.plusDays(5))
                .isActive(true)
                .build();

        when(couponRepository.findByIdWithFetch(couponId))
                .thenReturn(Optional.of(existingCoupon));

        // 새 정책 준비
        CouponPolicy newPolicy = CouponPolicy.builder().id(2L).discountPercentage(10).discountValue(0).build();
        when(policyRepository.findById(2L))
                .thenReturn(Optional.of(newPolicy));

        // couponRepository.save(coupon)이 수정된 쿠폰을 그대로 반환하도록 설정
        // 이 쿠폰의 isActive()는 true를 반환할 것임
        when(couponRepository.save(any(Coupon.class))).thenReturn(existingCoupon);

        // 활성 상태를 유지하는 요청
        CouponUpdateRequest req = new CouponUpdateRequest(
                2L, "Updated Active Coupon", now, newExpires,
                true,            // isActive=true!
                List.of(), List.of()
        );

        // when
        couponService.updateCoupon(couponId, req);

        // then
        // disableCouponStoresByCouponId가 호출되지 않아야 함
        verify(couponStoreService, never())
                .disableCouponStoresByCouponId(anyLong());
    }


    @Test
    @DisplayName("disableCouponStoresByCouponId — 조회된 각 Store의 상태를 업데이트하고 저장")
    void disableCouponStoresByCouponId_callsUpdateOnEachStore() {
        // given
        Long couponId = 10L;

        // READY 상태의 CouponStore 3개를 spy로 준비
        CouponStore store1 = spy(CouponStore.builder()
                .id(1L)
                .status(CouponStatus.READY)
                .build());
        CouponStore store2 = spy(CouponStore.builder()
                .id(2L)
                .status(CouponStatus.READY)
                .build());
        CouponStore store3 = spy(CouponStore.builder()
                .id(3L)
                .status(CouponStatus.READY)
                .build());
        List<CouponStore> stores = List.of(store1, store2, store3);

        // findAllBy... 리포지토리 stub
        when(couponStoreRepository.findAllByCouponIdAndStatus(couponId, CouponStatus.READY))
                .thenReturn(stores);
        // saveAll은 단순히 인자를 그대로 리턴하도록
        when(couponStoreRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        couponStoreService.disableCouponStoresByCouponId(couponId);

        // then
        // 1) 각 스토어에 대해 update(DISABLED, null)이 한 번씩 호출되었는지
        stores.forEach(store ->
                verify(store, times(1))
                        .update(eq(CouponStatus.DISABLED), isNull())
        );

        // 2) 변경된 리스트가 한 번만 저장되었는지
        verify(couponStoreRepository, times(1)).saveAll(stores);
    }



    @Test
    void deleteCouponById_delegatesToRepository() {
        couponService.deleteCouponById(100L);
        verify(couponRepository).deleteById(100L);
    }
}
