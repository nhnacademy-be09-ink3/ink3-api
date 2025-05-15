package shop.ink3.api.coupon.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/admin/coupons")
public class CouponController {

    private final CouponServiceImpl couponService;

    @PostMapping("/create")
    public ResponseEntity<CommonResponse<CouponResponse>> create(@RequestBody @Valid CouponCreateRequest request) {
        CouponResponse coupon = couponService.createCoupon(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(coupon));
    }
}

