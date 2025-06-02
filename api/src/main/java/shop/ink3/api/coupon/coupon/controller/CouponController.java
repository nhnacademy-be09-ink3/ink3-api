package shop.ink3.api.coupon.coupon.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.service.Impl.CouponServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponServiceImpl couponService;

    /**
     * Handles HTTP POST requests to create a new coupon.
     *
     * @param request the coupon creation request data
     * @return a ResponseEntity containing the created coupon wrapped in a CommonResponse, with HTTP status 201 Created
     */
    @PostMapping
    public ResponseEntity<CommonResponse<CouponResponse>> create(@RequestBody @Valid CouponCreateRequest request) {
        CouponResponse coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(coupon));
    }

    /**
     * Retrieves a coupon by its unique ID.
     *
     * @param couponId the ID of the coupon to retrieve
     * @return a response containing the coupon details wrapped in a CommonResponse
     */
    @GetMapping("/{couponId}")
    public ResponseEntity<CommonResponse<CouponResponse>> getById(@PathVariable long couponId) {
        CouponResponse resp = couponService.getCouponById(couponId);
        return ResponseEntity.ok(CommonResponse.success(resp));
    }

    /** 이름으로 쿠폰 조회 */
    @GetMapping("/by-name/{couponName}")
    public ResponseEntity<CommonResponse<List<CouponResponse>>> getByName(@PathVariable String couponName) {
        List<CouponResponse> list = couponService.getCouponByName(couponName);
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    /** 전체 쿠폰 조회 */
    @GetMapping
    public ResponseEntity<CommonResponse<List<CouponResponse>>> getAll() {
        List<CouponResponse> list = couponService.getAllCoupons();
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    /**
     * Deletes a coupon by its ID.
     *
     * @param couponId the ID of the coupon to delete
     * @return a response indicating successful deletion with no data
     */
    @DeleteMapping("/{couponId}")
    public ResponseEntity<CommonResponse<Void>> deleteById(@PathVariable long couponId) {
        couponService.deleteCouponById(couponId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }



}



