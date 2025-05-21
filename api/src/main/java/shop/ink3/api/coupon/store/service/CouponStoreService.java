package shop.ink3.api.coupon.store.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.store.dto.CouponStoreCreateRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateRequest;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.exception.CouponStoreNotFoundException;
import shop.ink3.api.coupon.store.exception.DuplicateCouponException;
import shop.ink3.api.coupon.store.repository.UserCouponRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class CouponStoreService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    /** 1) 쿠폰 저장소 생성 */
    public CouponStore createStore(CouponStoreCreateRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new UserNotFoundException(req.userId()));
        Coupon coupon = couponRepository.findById(req.couponId())
                .orElseThrow(() -> new CouponNotFoundException("쿠폰이 없습니다. id=" + req.couponId()));

        if (isAlreadyIssued(req.userId(), req.couponId())) {
            throw new DuplicateCouponException(
                    "이미 발급된 쿠폰입니다. user=" + req.userId() + ", coupon=" + req.couponId()
            );
        }

        CouponStore store = CouponStore.builder()
                .user(user)
                .coupon(coupon)
                .status(CouponStatus.READY)
                .usedAt(null)
                .issuedAt(LocalDateTime.now())
                .build();

        userCouponRepository.save(store);
        return store;
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

    /** fix 예정 */
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

    /** 7) 중복 쿠폰 발급 여부 확인 */
    @Transactional(readOnly = true)
    public boolean isAlreadyIssued(Long userId, Long couponId) {
        return userCouponRepository.existsByUserIdAndCouponId(userId, couponId);
    }

    /** 8) 정기적으로 발급되는 쿠폰 처리 (생일쿠폰) */
    @Transactional(readOnly = true)
    public boolean isAlreadyIssuedBirthday(Long userId, Long couponId) {

        return userCouponRepository.existsByUserIdAndCouponIdAndYear(
                userId, couponId, Year.now().getValue());
    }
}
