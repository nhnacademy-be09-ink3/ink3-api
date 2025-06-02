package shop.ink3.api.coupon.coupon.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;

public interface CouponService {

    /****
 * Creates a new coupon using the provided request data.
 *
 * @param coupon the details required to create the coupon
 * @return the created coupon information
 */
    CouponResponse createCoupon(CouponCreateRequest coupon);

    /****
 * Retrieves a coupon by its unique identifier.
 *
 * @param id the unique identifier of the coupon
 * @return the coupon corresponding to the given id
 */
    CouponResponse getCouponById(long id);

    // 쿠폰 조회 by name
    List<CouponResponse> getCouponByName(String couponName);

    // 모든 쿠폰 조회
    List<CouponResponse> getAllCoupons();

    /****
 * Deletes a coupon identified by its unique ID.
 *
 * @param couponId the unique identifier of the coupon to delete
 */
    void deleteCouponById(Long couponId);

}

