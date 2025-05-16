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
import shop.ink3.api.coupon.coupon.dto.CouponDeleteResponse;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;
import shop.ink3.api.coupon.coupon.service.Impl.CouponServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponServiceImpl couponService;

    /** 쿠폰 생성 */
    @PostMapping("/create")
    public ResponseEntity<CommonResponse<CouponResponse>> create(@RequestBody @Valid CouponCreateRequest request) {


        CouponResponse coupon = couponService.createCoupon(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(coupon));
    }

    /** 트리거 타입으로 쿠폰 조회 */
    @GetMapping("/trigger/{triggerType}")
    public ResponseEntity<CommonResponse<List<CouponResponse>>> getByTriggerType(
            @PathVariable TriggerType triggerType) {
        List<CouponResponse> list = couponService.getCouponByTriggerType(triggerType);
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    /** 이슈 타입으로 쿠폰 조회 */
    @GetMapping("/issue/{issueType}")
    public ResponseEntity<CommonResponse<List<CouponResponse>>> getByIssueType(
            @PathVariable IssueType issueType) {
        List<CouponResponse> list = couponService.getCouponByIssueType(issueType);
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    /** ID로 단건 조회 */
    @GetMapping("/{couponId}")
    public ResponseEntity<CommonResponse<CouponResponse>> getById(
            @PathVariable long couponId) {
        CouponResponse resp = couponService.getCouponById(couponId);
        return ResponseEntity.ok(CommonResponse.success(resp));
    }

    /** 이름으로 쿠폰 조회 */
    @GetMapping("/name/{couponName}")
    public ResponseEntity<CommonResponse<List<CouponResponse>>> getByName(
            @PathVariable String couponName) {
        List<CouponResponse> list = couponService.getCouponByCouponName(couponName);
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    /** 전체 쿠폰 조회 */
    @GetMapping
    public ResponseEntity<CommonResponse<List<CouponResponse>>> getAll() {
        List<CouponResponse> list = couponService.getAllCoupons();
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    @DeleteMapping("/name/{couponName}")
    public ResponseEntity<CommonResponse<CouponDeleteResponse>> deleteByName(
            @PathVariable String couponName
    ){
        CouponDeleteResponse deleteCoupon = couponService.deleteCouponByName(couponName);
        return ResponseEntity.ok(CommonResponse.success(deleteCoupon));
    }

    @DeleteMapping("/name/{couponId}")
    public ResponseEntity<CommonResponse<CouponDeleteResponse>> deleteById(
            @PathVariable long couponId
    ){
        CouponDeleteResponse deleteCoupon = couponService.deleteCouponById(couponId);
        return ResponseEntity.ok(CommonResponse.success(deleteCoupon));
    }



}


