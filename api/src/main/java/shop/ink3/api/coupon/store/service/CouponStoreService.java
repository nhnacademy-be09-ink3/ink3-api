//package shop.ink3.api.coupon.store.service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import shop.ink3.api.coupon.coupon.entity.Coupon;
//import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
//import shop.ink3.api.coupon.coupon.repository.CouponRepository;
//import shop.ink3.api.coupon.store.dto.CouponStoreCreateRequest;
//import shop.ink3.api.coupon.store.dto.CouponStoreUpdateRequest;
//import shop.ink3.api.coupon.store.entity.CouponStore;
//import shop.ink3.api.coupon.store.exception.CouponStoreNotFoundException;
//import shop.ink3.api.coupon.store.exception.DuplicateCouponException;
//import shop.ink3.api.coupon.store.repository.UserCouponRepository;
//import shop.ink3.api.user.user.entity.User;
//import shop.ink3.api.user.user.exception.UserNotFoundException;
//import shop.ink3.api.user.user.repository.UserRepository;
//
//@Transactional
//@RequiredArgsConstructor
//@Service
//public class CouponStoreService {
//
//    private final CouponRepository       couponRepository;
//    private final UserRepository         userRepository;
//    private final UserCouponRepository   userCouponRepository;
//
//    /** 1) 생성 */
//    public void createStore(CouponStoreCreateRequest req) {
//        // 1. User, Coupon 존재 확인
//        User user = userRepository.findById(req.userId())
//                .orElseThrow(() -> new UserNotFoundException(req.userId()));
//        Coupon coupon = couponRepository.findById(req.couponId())
//                .orElseThrow(() -> new CouponNotFoundException("쿠폰이 없습니다. id=" + req.couponId()));
//
//        // 2. 중복 체크
//        if (isAlreadyIssued(req.userId(), req.couponId())) {
//            throw new DuplicateCouponException(
//                    "이미 발급된 쿠폰입니다. user=" + req.userId() + ", coupon=" + req.couponId()
//            );
//        }
//
//        // 3. 엔티티 빌드
//        LocalDateTime now = LocalDateTime.now();
//        CouponStore store = CouponStore.builder()
//                .user(user)
//                .coupon(coupon)
//                .createdAt(now)
//                .isUsed(false)
//                .usedAt(null)
//                .build();
//
//        // 4. 저장
//        userCouponRepository.save(store);
//    }
//
//    /** 2) 유저별 전체 조회 */
//    @Transactional(readOnly = true)
//    public List<CouponStore> getStoreByUserId(Long userId) {
//        return userCouponRepository.findByUserId(userId);
//    }
//
//    /** 3) 쿠폰별 조회 */
//    @Transactional(readOnly = true)
//    public List<CouponStore> getStoreByCouponId(Long couponId) {
//        return userCouponRepository.findByCouponId(couponId);
//    }
//
//    /** 4) 미사용 쿠폰만 조회 */
//    @Transactional(readOnly = true)
//    public List<CouponStore> getStoreByUnUsedCoupon(Long userId) {
//        return userCouponRepository.findByUserIdAndUsedFalse(userId);
//    }
//
//    /** 5) 사용 상태 업데이트 */
//    public void updateStore(Long storeId, CouponStoreUpdateRequest req) {
//        CouponStore store = userCouponRepository.findById(storeId)
//                .orElseThrow(() -> new CouponStoreNotFoundException(storeId+" coupon not found"));
//
//        store.setUsed(req.isUsed());
//        if (req.isUsed()) {
//            store.setUsedAt(req.usedAt());
//        }
//        // 변경 감지로 자동 업데이트
//    }
//
//    /** 6) 삭제 */
//    public void deleteStore(Long id) {
//        if (!userCouponRepository.existsById(id)) {
//            throw new CouponStoreNotFoundException(id + " coupon not found");
//        }
//        userCouponRepository.deleteById(id);
//    }
//
//    /** 발급 여부 확인 **/
//    @Transactional(readOnly = true)
//    public boolean isAlreadyIssued(Long userId, Long couponId) {
//        return userCouponRepository.existsByUserIdAndCouponId(userId, couponId);
//    }
//
//}
//
