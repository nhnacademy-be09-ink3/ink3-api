package shop.ink3.api.coupon.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.test.util.ReflectionTestUtils;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.bookCategory.entity.BookCategory;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.coupon.store.dto.CouponIssueRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateRequest;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.entity.OriginType;
import shop.ink3.api.coupon.store.exception.CouponStoreNotFoundException;
import shop.ink3.api.coupon.store.exception.DuplicateCouponException;
import shop.ink3.api.coupon.store.repository.CouponStoreRepository;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CouponStoreServiceTest {

    @Mock private CouponRepository couponRepository;
    @Mock private UserRepository userRepository;
    @Mock private CouponStoreRepository userCouponRepository;
    @Mock private BookCouponRepository bookCouponRepository;
    @Mock private CategoryCouponRepository categoryCouponRepository;
    @Mock private CouponStoreRepository couponStoreRepository;
    @Mock private BookRepository bookRepository;

    @InjectMocks private CouponStoreService couponStoreService;

    private User dummyUser;
    private User dummyUser2;
    private Coupon dummyCoupon;
    private Coupon dummyCoupon2;

    @BeforeEach
    void setUp() {
        this.dummyUser = User.builder().id(1L).build();
        this.dummyUser2 = User.builder().id(2L).build();
        this.dummyCoupon = Coupon.builder().id(2L).build();
        this.dummyCoupon2 = Coupon.builder().id(2L).build();
    }

    @Test
    @DisplayName("issueCoupon - originId 있을 때 성공")
    void issueCoupon_withOriginId_success() {
        // given
        CouponIssueRequest req = new CouponIssueRequest(
                1L,     // userId
                2L,     // couponId
                OriginType.BOOK,
                100L    // originId
        );

        // 1) userRepository.findById, couponRepository.findById 모킹
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(couponRepository.findById(2L)).thenReturn(Optional.of(dummyCoupon2));

        // 2) 중복 검사 모킹 (originId != null)
        when(couponStoreRepository.existsByUserIdAndCouponIdAndOriginTypeAndOriginId(
                1L, 2L, OriginType.BOOK, 100L))
                .thenReturn(false);

        // 3) save(...) 호출 시 인자로 전달된 CouponStore 객체에 id=1L을 설정
        when(couponStoreRepository.save(any(CouponStore.class)))
                .thenAnswer(invocation -> {
                    CouponStore arg = invocation.getArgument(0);
                    // private 필드인 id에 값을 설정
                    ReflectionTestUtils.setField(arg, "id", 1L);
                    return arg;
                });

        // when
        CouponStore result = couponStoreService.issueCoupon(req);

        // then
        assertNotNull(result, "반환된 CouponStore가 null이어서는 안 됩니다.");
        assertEquals(1L, result.getId(), "저장된 CouponStore의 ID는 1이어야 합니다.");

        verify(couponStoreRepository).existsByUserIdAndCouponIdAndOriginTypeAndOriginId(
                1L, 2L, OriginType.BOOK, 100L);
        verify(couponStoreRepository).save(any(CouponStore.class));
    }



    @Test
    @DisplayName("issueCoupon - originId null인데 이미 발급되어 예외")
    void issueCoupon_originIdNull_duplicateThrows() {
        // given
        CouponIssueRequest req = new CouponIssueRequest(
                1L,
                2L,
                OriginType.WELCOME,
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(couponRepository.findById(2L)).thenReturn(Optional.of(dummyCoupon2));
        when(couponStoreRepository.existsByUserIdAndOriginType(1L, OriginType.WELCOME))
                .thenReturn(true);

        // when / then
        assertThrows(DuplicateCouponException.class, () -> couponStoreService.issueCoupon(req));

        verify(couponStoreRepository).existsByUserIdAndOriginType(1L, OriginType.WELCOME);
        verify(couponStoreRepository, never()).save(any(CouponStore.class));
    }

    @Test
    @DisplayName("issueCoupon - user not found 예외")
    void issueCoupon_userNotFound() {
        // given
        CouponIssueRequest req = new CouponIssueRequest(99L, 2L, OriginType.BOOK, 100L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(UserNotFoundException.class, () -> couponStoreService.issueCoupon(req));
        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(couponRepository, couponStoreRepository);
    }

    @Test
    @DisplayName("issueCoupon - coupon not found 예외")
    void issueCoupon_couponNotFound() {
        // given
        CouponIssueRequest req = new CouponIssueRequest(1L, 99L, OriginType.BOOK, 100L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(couponRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(CouponNotFoundException.class, () -> couponStoreService.issueCoupon(req));
        verify(userRepository).findById(1L);
        verify(couponRepository).findById(99L);
        verifyNoMoreInteractions(couponStoreRepository);
    }

    @Test
    @DisplayName("getStoresByUserId - 정상 조회")
    void getStoresByUserId_success() {
        List<CouponStore> stores = List.of(
                CouponStore.builder().id(1L).user(dummyUser).build(),
                CouponStore.builder().id(2L).user(dummyUser2).build()
        );
        when(userCouponRepository.findByUserId(1L)).thenReturn(stores);

        List<CouponStore> result = couponStoreService.getStoresByUserId(1L);

        assertEquals(stores, result);
        verify(userCouponRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("getStoresByCouponId - 정상 조회")
    void getStoresByCouponId_success() {
        List<CouponStore> stores = List.of(
                CouponStore.builder().id(1L).build(),
                CouponStore.builder().id(2L).build()
        );
        when(userCouponRepository.findByCouponId(2L)).thenReturn(stores);

        List<CouponStore> result = couponStoreService.getStoresByCouponId(2L);

        assertEquals(stores, result);
        verify(userCouponRepository).findByCouponId(2L);
    }

    @Test
    @DisplayName("getUnusedStoresByUserId - 정상 조회")
    void getUnusedStoresByUserId_success() {
        List<CouponStore> stores = List.of(
                CouponStore.builder().id(1L).user(dummyUser).coupon(dummyCoupon).status(CouponStatus.READY).build()
        );
        when(userCouponRepository.findByUserIdAndStatus(1L, CouponStatus.READY))
                .thenReturn(stores);

        List<CouponStore> result = couponStoreService.getUnusedStoresByUserId(1L);

        assertEquals(stores, result);
        verify(userCouponRepository).findByUserIdAndStatus(1L, CouponStatus.READY);
    }

    @Test
    @DisplayName("updateStore - 정상 변경")
    void updateStore_success() {
        CouponStore existing = CouponStore.builder()
                .id(3L)
                .status(CouponStatus.READY)
                .issuedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(couponStoreRepository.findById(3L)).thenReturn(Optional.of(existing));

        CouponStoreUpdateRequest req = new CouponStoreUpdateRequest(
                CouponStatus.USED,
                LocalDateTime.of(2025, 5, 31, 0, 0)
        );

        CouponStore result = couponStoreService.updateStore(3L, req);

        assertEquals(CouponStatus.USED, result.getStatus());
        assertEquals(req.usedAt(), result.getUsedAt());
        verify(couponStoreRepository).findById(3L);
    }

    @Test
    @DisplayName("updateStore - 없는 storeId 예외")
    void updateStore_notFoundThrows() {
        when(couponStoreRepository.findById(10L)).thenReturn(Optional.empty());
        CouponStoreUpdateRequest req = new CouponStoreUpdateRequest(
                CouponStatus.USED,
                LocalDateTime.now()
        );

        assertThrows(CouponStoreNotFoundException.class,
                () -> couponStoreService.updateStore(10L, req));

        verify(couponStoreRepository).findById(10L);
        verifyNoMoreInteractions(couponStoreRepository);
    }

    @Test
    @DisplayName("deleteStore - 정상 삭제")
    void deleteStore_success() {
        // deleteById가 예외 없이 수행됨
        doNothing().when(couponStoreRepository).deleteById(7L);

        assertDoesNotThrow(() -> couponStoreService.deleteStore(7L));

        verify(couponStoreRepository).deleteById(7L);
    }

    @Test
    @DisplayName("deleteStore - 없는 storeId 예외")
    void deleteStore_notFoundThrows() {
        doThrow(new EmptyResultDataAccessException(1)).when(couponStoreRepository).deleteById(9L);

        assertThrows(CouponStoreNotFoundException.class,
                () -> couponStoreService.deleteStore(9L));

        verify(couponStoreRepository).deleteById(9L);
    }

    @Test
    @DisplayName("getApplicableCouponStores - 여러 조건 필터링")
    void getApplicableCouponStores_success() {
        Long userId = 1L;
        Long bookId = 100L;

        // 1) BOOK origin
        List<Long> bookCouponIds = List.of(11L, 12L);
        when(bookCouponRepository.findIdsByBookId(bookId))
                .thenReturn(bookCouponIds);

        Coupon validBookCoupon = Coupon.builder()
                .id(101L)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        CouponStore validBookStore = CouponStore.builder()
                .coupon(validBookCoupon)
                .status(CouponStatus.READY)
                .build();

        Coupon expiredBookCoupon = Coupon.builder()
                .id(102L)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();
        CouponStore expiredBookStore = CouponStore.builder()
                .coupon(expiredBookCoupon)
                .status(CouponStatus.READY)
                .build();

        when(couponStoreRepository.findByUserIdAndOriginTypeAndOriginIdInAndStatus(
                userId, OriginType.BOOK, bookCouponIds, CouponStatus.READY
        )).thenReturn(List.of(validBookStore, expiredBookStore));

        // 2) CATEGORY origin
        // 실제 Book 객체를 만든 뒤, spy로 감싸서 getBookCategories()만 따로 스텁
        Book realBook = Book.builder().id(bookId).build();
        Book spyBook = spy(realBook);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(spyBook));

        Category cat = Category.builder().id(20L).build();
        BookCategory bc = new BookCategory(spyBook, cat);
        // getBookCategories()가 List<BookCategory>를 반환하도록 doReturn 사용
        doReturn(List.of(bc)).when(spyBook).getBookCategories();

        List<Long> categoryCouponIds = List.of(21L);
        when(categoryCouponRepository.findIdsByCategoryId(20L))
                .thenReturn(categoryCouponIds);

        Coupon validCatCoupon = Coupon.builder()
                .id(201L)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();
        CouponStore validCatStore = CouponStore.builder()
                .coupon(validCatCoupon)
                .status(CouponStatus.READY)
                .build();

        when(couponStoreRepository.findByUserIdAndOriginTypeAndOriginIdInAndStatus(
                userId, OriginType.CATEGORY, categoryCouponIds, CouponStatus.READY
        )).thenReturn(List.of(validCatStore));

        // 3) WELCOME
        Coupon welcomeCoupon = Coupon.builder()
                .id(301L)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        CouponStore welcomeStore = CouponStore.builder()
                .coupon(welcomeCoupon)
                .status(CouponStatus.READY)
                .build();
        when(couponStoreRepository.findByUserIdAndOriginTypeAndStatus(
                userId, OriginType.WELCOME, CouponStatus.READY
        )).thenReturn(List.of(welcomeStore));

        // 4) BIRTHDAY (만료된 쿠폰)
        Coupon birthdayCoupon = Coupon.builder()
                .id(401L)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();
        CouponStore birthdayStore = CouponStore.builder()
                .coupon(birthdayCoupon)
                .status(CouponStatus.READY)
                .build();
        when(couponStoreRepository.findByUserIdAndOriginTypeAndStatus(
                userId, OriginType.BIRTHDAY, CouponStatus.READY
        )).thenReturn(List.of(birthdayStore));

        // when
        List<CouponStore> result = couponStoreService.getApplicableCouponStores(userId, bookId);

        // then: 만료된 BOOK 쿠폰과 BIRTHDAY 쿠폰은 필터링
        assertEquals(3, result.size());
        assertTrue(result.contains(validBookStore));
        assertTrue(result.contains(validCatStore));
        assertTrue(result.contains(welcomeStore));
        assertFalse(result.contains(expiredBookStore));
        assertFalse(result.contains(birthdayStore));

        verify(bookCouponRepository).findIdsByBookId(bookId);
        verify(couponStoreRepository).findByUserIdAndOriginTypeAndOriginIdInAndStatus(
                eq(userId), eq(OriginType.BOOK), eq(bookCouponIds), eq(CouponStatus.READY)
        );
        verify(bookRepository).findById(bookId);
        verify(spyBook).getBookCategories();
        verify(categoryCouponRepository).findIdsByCategoryId(20L);
        verify(couponStoreRepository).findByUserIdAndOriginTypeAndOriginIdInAndStatus(
                eq(userId), eq(OriginType.CATEGORY), eq(categoryCouponIds), eq(CouponStatus.READY)
        );
        verify(couponStoreRepository).findByUserIdAndOriginTypeAndStatus(
                userId, OriginType.WELCOME, CouponStatus.READY
        );
        verify(couponStoreRepository).findByUserIdAndOriginTypeAndStatus(
                userId, OriginType.BIRTHDAY, CouponStatus.READY
        );
    }
}
