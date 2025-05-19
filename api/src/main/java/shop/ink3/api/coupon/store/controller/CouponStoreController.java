package shop.ink3.api.coupon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.coupon.store.dto.CouponStoreCreateRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateResponse;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.service.CouponStoreService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class CouponStoreController {

    private final CouponStoreService couponStoreService;

    // 쿠폰 발급
    @PostMapping
    public ResponseEntity<CommonResponse<CouponStoreResponse>> createCouponStore(@RequestBody CouponStoreCreateRequest request) {
        CouponStore store = couponStoreService.createStore(request);
        CouponStoreResponse response = CouponStoreResponse.fromEntity(store);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(response));
    }

    // 유저 ID로 전체 쿠폰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<List<CouponStoreResponse>>> getStoresByUserId(@PathVariable Long userId) {
        List<CouponStore> storeList = couponStoreService.getStoresByUserId(userId);

        // Entity → DTO 변환
        List<CouponStoreResponse> responseList = storeList.stream()
                .map(CouponStoreResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(CommonResponse.success(responseList));
    }


    // 유저 ID로 미사용 쿠폰 조회
    @GetMapping("/user/{userId}/unused")
    public ResponseEntity<CommonResponse<List<CouponStoreResponse>>> getUnusedStores(@PathVariable Long userId) {
        List<CouponStore> storeList = couponStoreService.getUnusedStoresByUserId(userId);
        List<CouponStoreResponse> responseList = storeList.stream()
                .map(CouponStoreResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(responseList));
    }

    // 쿠폰 ID로 전체 조회
    @GetMapping("/coupon/{couponId}")
    public ResponseEntity<CommonResponse<List<CouponStoreResponse>>> getStoresByCouponId(@PathVariable Long couponId) {
        List<CouponStore> storeList = couponStoreService.getStoresByCouponId(couponId);
        List<CouponStoreResponse> responseList = storeList.stream()
                .map(CouponStoreResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(responseList));
    }


    // 쿠폰 사용 여부 업데이트
    @PutMapping("/{storeId}")
    public ResponseEntity<CommonResponse<CouponStoreUpdateResponse>> updateStore(
            @PathVariable Long storeId,
            @RequestBody CouponStoreUpdateRequest request) {
        CouponStoreUpdateResponse response = CouponStoreUpdateResponse.of(couponStoreService.updateStore(storeId, request));

        return ResponseEntity.ok(CommonResponse.update(response));
    }

    // 쿠폰 발급 삭제
    @DeleteMapping("/{storeId}")
    public ResponseEntity<CommonResponse<Void>> deleteStore(@PathVariable Long storeId) {
        couponStoreService.deleteStore(storeId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }
}
