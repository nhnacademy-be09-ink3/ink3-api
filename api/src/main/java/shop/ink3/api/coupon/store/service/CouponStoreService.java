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

    /****
     * Issues a coupon to a user, ensuring that the coupon has not already been issued with the same origin type and origin ID.
     *
     * If a coupon with the specified user, coupon, origin type, and (optionally) origin ID already exists, no duplicate is created. Otherwise, a new coupon store entry is created with status READY and the current issue timestamp.
     *
     * @param request the coupon issuance request containing user ID, coupon ID, origin type, and optionally origin ID
     * @return the newly issued CouponStore entity
     */
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

    /****
     * Retrieves all coupon stores associated with a specific coupon ID.
     *
     * @param couponId the ID of the coupon
     * @return a list of CouponStore entities linked to the given coupon ID
     */
    @Transactional(readOnly = true)
    public List<CouponStore> getStoresByCouponId(Long couponId) {
        // 이 메서드가 없다면 UserCouponRepository에 추가해야 함
        return userCouponRepository.findByCouponId(couponId);
    }

    /**
     * Retrieves all unused coupon stores for a given user.
     *
     * @param userId the ID of the user whose unused coupons are to be retrieved
     * @return a list of coupon stores with status READY for the specified user
     */
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

    /**
     * Deletes a coupon store by its ID.
     *
     * @param id the ID of the coupon store to delete
     * @throws CouponStoreNotFoundException if the coupon store does not exist
     */
    public void deleteStore(Long id) {
        if (!userCouponRepository.existsById(id)) {
            throw new CouponStoreNotFoundException(id + " coupon not found");
        }
        userCouponRepository.deleteById(id);
    }

    /**
     * Retrieves all applicable and unexpired coupon stores for a user based on book and category.
     *
     * Gathers coupons issued to the user that are associated with the specified book, category, or have a WELCOME or BIRTHDAY origin, and are in READY status. Filters out coupons that have expired.
     *
     * @param userId the ID of the user
     * @param bookId the ID of the book to check for applicable coupons
     * @param categoryId the ID of the category to check for applicable coupons
     * @return a list of applicable and unexpired CouponStore entities for the user
     */
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
