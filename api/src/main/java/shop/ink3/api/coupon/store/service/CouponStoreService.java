package shop.ink3.api.coupon.store.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.store.dto.CouponIssueRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateRequest;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.entity.OriginType;
import shop.ink3.api.coupon.store.exception.CouponStoreNotFoundException;
import shop.ink3.api.coupon.store.exception.DuplicateCouponException;
import shop.ink3.api.coupon.store.repository.CouponStoreRepository;
import shop.ink3.api.user.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class CouponStoreService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponStoreRepository userCouponRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;
    private final CouponStoreRepository couponStoreRepository;

    /** 1) 쿠폰 발급 */
    public CouponStore issueCoupon(CouponIssueRequest request) {
        boolean isIssued;

        if (request.originId() == null) {
            isIssued = userCouponRepository.existsByUserIdAndCouponIdAndOriginTypeAndOriginIdIsNull(
                    request.userId(), request.couponId(), request.originType()
            );
        } else {
            isIssued = userCouponRepository.existsByUserIdAndCouponIdAndOriginTypeAndOriginId(
                    request.userId(), request.couponId(), request.originType(), request.originId()
            );
        }
        CouponStore couponStore = CouponStore.builder()
                .user(userRepository.getReferenceById(request.userId()))
                .coupon(couponRepository.getReferenceById(request.couponId()))
                .originType(request.originType())
                .originId(request.originId())
                .status(CouponStatus.READY)
                .usedAt(null)
                .issuedAt(LocalDateTime.now())
                .build();
        couponStoreRepository.save(couponStore);
        return couponStore;
    }

    /** 2) 유저의 모든 쿠폰 조회 */
    @Transactional(readOnly = true)
    public List<CouponStore> getStoresByUserId(Long userId) {
        return userCouponRepository.findByUserId(userId);
    }

    /** 3) 특정 쿠폰을 가진 유저들 조회 */
    @Transactional(readOnly = true)
    public List<CouponStore> getStoresByCouponId(Long couponId) {
        // 이 메서드가 없다면 UserCouponRepository에 추가해야 함
        return userCouponRepository.findByCouponId(couponId);
    }

    /** 4) 미사용 쿠폰만 조회 */
    @Transactional(readOnly = true)
    public List<CouponStore> getUnusedStoresByUserId(Long userId) {

        return userCouponRepository.findByUserIdAndStatus(userId, CouponStatus.READY);
    }

    /** 5) 사용 여부 업데이트 */
    public CouponStore updateStore(Long storeId, CouponStoreUpdateRequest req) {
        CouponStore store = userCouponRepository.findById(storeId)
                .orElseThrow(() -> new CouponStoreNotFoundException(storeId + " coupon not found"));
        store.setStatus(req.couponStatus());
        store.setUsedAt(req.usedAt());

        return store;
    }

    /** 6) 삭제 */
    public void deleteStore(Long id) {
        if (!userCouponRepository.existsById(id)) {
            throw new CouponStoreNotFoundException(id + " coupon not found");
        }
        userCouponRepository.deleteById(id);
    }

    /** 7) 상품에 적용가능한 쿠폰 조회 */
    @Transactional(readOnly = true)
    public List<CouponStore> getApplicableCouponStores(Long userId, Long bookId, Long categoryId) {
        // 1) book origin
        List<Long> bookCouponIds = bookCouponRepository.findIdsByBookId(bookId);
        List<CouponStore> bookStores = couponStoreRepository
                .findByUserIdAndOriginTypeAndOriginIdInAndStatus(
                        userId, OriginType.BOOK, bookCouponIds, CouponStatus.READY);

        // 2) category origin
        List<Long> categoryCouponIds = categoryCouponRepository.findIdsByCategoryId(categoryId);
        List<CouponStore> categoryStores = couponStoreRepository
                .findByUserIdAndOriginTypeAndOriginIdInAndStatus(
                        userId, OriginType.CATEGORY, categoryCouponIds, CouponStatus.READY);

        // 3) welcome
        List<CouponStore> welcomeStores = couponStoreRepository
                .findByUserIdAndOriginTypeAndStatus(userId, OriginType.WELCOME, CouponStatus.READY);

        // 4) birthday
        List<CouponStore> birthdayStores = couponStoreRepository
                .findByUserIdAndOriginTypeAndStatus(userId, OriginType.BIRTHDAY, CouponStatus.READY);

        // 결합 후 쿠폰 유효성 필터링
        return Stream.of(bookStores, categoryStores, welcomeStores, birthdayStores)
                .flatMap(List::stream)
                .filter(store -> store.getCoupon().getExpiresAt().isAfter(LocalDateTime.now()))
                .toList();
    }

}
