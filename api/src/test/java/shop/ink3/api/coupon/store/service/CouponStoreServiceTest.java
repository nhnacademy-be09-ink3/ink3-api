package shop.ink3.api.coupon.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.bookCategory.entity.BookCategory;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponService;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;
import shop.ink3.api.coupon.store.dto.CommonCouponIssueRequest;
import shop.ink3.api.coupon.store.dto.CouponIssueRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateRequest;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.entity.OriginType;
import shop.ink3.api.coupon.store.exception.CouponStoreNotFoundException;
import shop.ink3.api.coupon.store.exception.DuplicateCouponException;
import shop.ink3.api.coupon.store.repository.CouponStoreRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CouponStoreServiceTest {

    @Mock private CouponRepository couponRepository;
    @Mock private UserRepository userRepository;
    @Mock private CouponStoreRepository couponStoreRepository;
    @Mock private BookCouponRepository bookCouponRepository;
    @Mock private CategoryCouponService categoryCouponService;
    @Mock private BookRepository bookRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks private CouponStoreService couponStoreService;

    private User user;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        lenient().when(user.getId()).thenReturn(1L);

        coupon = mock(Coupon.class);
        lenient().when(coupon.getId()).thenReturn(100L);
    }

    // ===========================
    // issueCoupon() 테스트
    // ===========================

    @Test
    void issueCoupon_withOriginId_succeeds() {
        Long userId = 1L;
        Long couponId = 100L;
        OriginType originType = OriginType.BOOK;
        Long originId = 200L;
        var request = new CommonCouponIssueRequest(userId, couponId, originType, originId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponStoreRepository.existsByUserIdAndCouponIdAndOriginTypeAndOriginId(
                userId, couponId, originType, originId)).thenReturn(false);

        CouponStore result = couponStoreService.issueCommonCoupon(request);

        assertNotNull(result);
        assertEquals(originType, result.getOriginType());
        assertEquals(originId, result.getOriginId());
        assertEquals(CouponStatus.READY, result.getStatus());
        verify(couponStoreRepository).save(any(CouponStore.class));
    }

    @Test
    void issueCoupon_withOriginId_duplicate_throwsDuplicateCouponException() {
        Long userId = 1L;
        Long couponId = 100L;
        OriginType originType = OriginType.BOOK;
        Long originId = 200L;
        var request = new CouponIssueRequest(couponId, originType, originId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponStoreRepository.existsByUserIdAndCouponIdAndOriginTypeAndOriginId(
                userId, couponId, originType, originId)).thenReturn(true);

        assertThrows(DuplicateCouponException.class,
                () -> couponStoreService.issueCoupon(request, 1L));
        verify(couponStoreRepository, never()).save(any());
    }

    @Test
    void issueCoupon_withoutOriginId_succeeds() {
        Long userId = 1L;
        Long couponId = 101L;
        OriginType originType = OriginType.WELCOME;
        var request = new CommonCouponIssueRequest(userId, couponId, originType, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponStoreRepository.existsByUserIdAndOriginType(userId, originType)).thenReturn(false);

        CouponStore result = couponStoreService.issueCommonCoupon(request);

        assertNotNull(result);
        assertEquals(originType, result.getOriginType());
        assertNull(result.getOriginId());
        assertEquals(CouponStatus.READY, result.getStatus());
        verify(couponStoreRepository).save(any(CouponStore.class));
    }

    @Test
    void issueCoupon_withoutOriginId_duplicate_throwsDuplicateCouponException() {
        Long userId = 1L;
        Long couponId = 102L;
        OriginType originType = OriginType.WELCOME;
        var request = new CommonCouponIssueRequest(userId, couponId, originType, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponStoreRepository.existsByUserIdAndOriginType(userId, originType)).thenReturn(true);

        assertThrows(DuplicateCouponException.class,
                () -> couponStoreService.issueCommonCoupon(request));
        verify(couponStoreRepository, never()).save(any());
    }

    @Test
    void issueCoupon_userNotFound_throwsUserNotFoundException() {
        Long userId = 9999L;
        Long couponId = 100L;
        var request = new CouponIssueRequest(couponId, OriginType.BOOK, 200L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> couponStoreService.issueCoupon(request, userId));
        verify(couponRepository, never()).findById(any());
        verify(couponStoreRepository, never()).save(any());
    }

    @Test
    void issueCoupon_couponNotFound_throwsCouponNotFoundException() {
        Long userId = 1L;
        Long couponId = 9999L;
        var request = new CouponIssueRequest(couponId, OriginType.BOOK, 200L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class,
                () -> couponStoreService.issueCoupon(request, userId));
        verify(couponStoreRepository, never()).save(any());
    }

    // ====================================
    // getStoresByUserId(), getStoresByCouponId(),
    // getUnusedStoresByUserId() 테스트
    // ====================================

    @Test
    void getStoresByUserId_returnsList() {
        Long userId = 1L;
        var sampleStore = CouponStore.builder()
                .id(1L)
                .user(user)
                .coupon(coupon)
                .originType(OriginType.WELCOME)
                .status(CouponStatus.READY)
                .issuedAt(LocalDateTime.now())
                .build();
        var stores = List.of(sampleStore);

        when(couponStoreRepository.findByUserId(userId)).thenReturn(stores);

        var result = couponStoreService.getStoresByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(stores, result);
    }

    @Test
    void getStoresByCouponId_returnsList() {
        Long couponId = 100L;
        var sampleStore = CouponStore.builder()
                .id(2L)
                .user(user)
                .coupon(coupon)
                .originType(OriginType.BIRTHDAY)
                .status(CouponStatus.READY)
                .issuedAt(LocalDateTime.now())
                .build();
        var stores = List.of(sampleStore);

        when(couponStoreRepository.findByCouponId(couponId)).thenReturn(stores);

        var result = couponStoreService.getStoresByCouponId(couponId);

        assertEquals(1, result.size());
        assertEquals(stores, result);
    }

    @Test
    void getUnusedStoresByUserId_returnsOnlyReady() {
        Long userId = 1L;
        var store1 = CouponStore.builder()
                .id(3L)
                .user(user)
                .coupon(coupon)
                .originType(OriginType.WELCOME)
                .status(CouponStatus.READY)
                .issuedAt(LocalDateTime.now())
                .build();
        var stores = List.of(store1);

        when(couponStoreRepository.findByUserIdAndStatus(userId, CouponStatus.READY)).thenReturn(stores);

        var result = couponStoreService.getUnusedStoresByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(stores, result);
    }

    // ===========================
    // updateStore() 테스트
    // ===========================

    @Test
    void updateStore_success() {
        Long storeId = 1L;
        var existingStore = CouponStore.builder()
                .id(storeId)
                .user(user)
                .coupon(coupon)
                .originType(OriginType.WELCOME)
                .status(CouponStatus.READY)
                .issuedAt(LocalDateTime.now())
                .build();
        when(couponStoreRepository.findById(storeId)).thenReturn(Optional.of(existingStore));

        CouponStoreUpdateRequest updateRequest = new CouponStoreUpdateRequest(CouponStatus.USED, LocalDateTime.now());

        var updated = couponStoreService.updateStore(storeId, updateRequest);

        assertEquals(CouponStatus.USED, updated.getStatus());
        assertNotNull(updated.getUsedAt());
    }

    @Test
    void updateStore_notFound_throwsCouponStoreNotFoundException() {
        Long storeId = 999L;
        when(couponStoreRepository.findById(storeId)).thenReturn(Optional.empty());

        CouponStoreUpdateRequest updateRequest = new CouponStoreUpdateRequest(CouponStatus.USED, LocalDateTime.now());

        assertThrows(CouponStoreNotFoundException.class,
                () -> couponStoreService.updateStore(storeId, updateRequest));
    }

    // ===========================
    // deleteStore() 테스트
    // ===========================

    @Test
    void deleteStore_success() {
        Long storeId = 1L;
        doNothing().when(couponStoreRepository).deleteById(storeId);

        assertDoesNotThrow(() -> couponStoreService.deleteStore(storeId));
        verify(couponStoreRepository).deleteById(storeId);
    }

    @Test
    void deleteStore_notFound_throwsCouponStoreNotFoundException() {
        Long storeId = 999L;
        doThrow(new EmptyResultDataAccessException(1))
                .when(couponStoreRepository).deleteById(storeId);

        assertThrows(CouponStoreNotFoundException.class,
                () -> couponStoreService.deleteStore(storeId));
    }

    // =========================================
    // getApplicableCouponStores() 간단 검증
    // =========================================

    @Test
    void getApplicableCouponStores_filtersExpiredAndCombinesOrigins() {
        Long userId = 1L;
        Long bookId = 10L;

        // 1) BOOK 기반 쿠폰 IDs
        var bookCouponIds = List.of(100L, 101L);
        when(bookCouponRepository.findIdsByBookId(bookId)).thenReturn(bookCouponIds);

        // 2) category IDs (book.getBookCategories 으로부터)
        var bookEntity = mock(Book.class);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        var bc1 = mock(Category.class);
        when(bc1.getId()).thenReturn(5L);
        var directCategoryIds = List.of(5L);
        when(bookEntity.getBookCategories()).thenReturn(List.of(
                new BookCategory(bc1) // assume constructor or setter
        ));
        // 조상 카테고리 없다고 가정
        when(categoryRepository.findAllAncestors(5L)).thenReturn(List.of());

        // 3) WELCOME, 4) BIRTHDAY IDs: 각각 간단히 빈 리스트로
        // Stub findWithCouponByUserAndOriginAndStatus for each origin
        var now = LocalDateTime.now();
        var policy = mock(CouponPolicy.class);
        when(policy.getDiscountType()).thenReturn(DiscountType.RATE);
        when(policy.getDiscountValue()).thenReturn(null);
        when(policy.getDiscountPercentage()).thenReturn(10);
        when(policy.getMaximumDiscountAmount()).thenReturn(1000);

        var coupon = mock(Coupon.class);
        when(coupon.getCouponPolicy()).thenReturn(policy);
        var validStore = CouponStore.builder()
                .id(1L)
                .user(user)
                .coupon(coupon)
                .originType(OriginType.BOOK)
                .status(CouponStatus.READY)
                .issuedAt(LocalDateTime.now())
                .build();
        // 만료되지 않은 쿠폰(ExpiresAt이 미래)
        when(validStore.getCoupon().getExpiresAt()).thenReturn(now.plusDays(1));

        var expiredCoupon = mock(Coupon.class);
        var expiredStore = CouponStore.builder()
                .id(2L)
                .user(user)
                .coupon(expiredCoupon)
                .originType(OriginType.BOOK)
                .status(CouponStatus.READY)
                .issuedAt(LocalDateTime.now())
                .build();
        // 만료된 쿠폰(ExpiresAt이 과거)
        when(expiredStore.getCoupon().getExpiresAt()).thenReturn(now.minusDays(1));

        // 책 기반: 하나는 유효, 하나는 만료
        when(couponStoreRepository.findWithCouponByUserAndOriginAndStatus(
                userId, OriginType.BOOK, bookCouponIds, CouponStatus.READY))
                .thenReturn(List.of(validStore, expiredStore));

        // CATEGORY 기반은 빈 리스트
        when(couponStoreRepository.findWithCouponByUserAndOriginAndStatus(
                userId, OriginType.CATEGORY, List.of(), CouponStatus.READY))
                .thenReturn(List.of());

        // WELCOME, BIRTHDAY도 빈 리스트
        when(couponStoreRepository.findWithCouponByUserAndOriginAndStatus(
                userId, OriginType.WELCOME, CouponStatus.READY))
                .thenReturn(List.of());
        when(couponStoreRepository.findWithCouponByUserAndOriginAndStatus(
                userId, OriginType.BIRTHDAY, CouponStatus.READY))
                .thenReturn(List.of());

        var dtoList = couponStoreService.getApplicableCouponStores(userId, bookId);

        // 유효한 store 하나만 반환
        assertEquals(1, dtoList.size());
        assertEquals(validStore.getId(), dtoList.get(0).storeId());
    }
}
