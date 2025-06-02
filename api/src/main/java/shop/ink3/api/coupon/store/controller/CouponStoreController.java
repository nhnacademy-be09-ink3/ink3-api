package shop.ink3.api.coupon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.store.dto.CouponIssueRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreUpdateResponse;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.entity.OriginType;
import shop.ink3.api.coupon.store.service.CouponStoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponStoreController {

    private final CouponStoreService couponStoreService;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;

    /**
     * Issues a new coupon store entry for a user.
     *
     * Accepts a coupon issuance request, delegates creation to the service layer, and returns the created coupon store details with HTTP 201 status.
     *
     * @param request the coupon issuance request containing user and coupon information
     * @return the created coupon store details wrapped in a common response
     */
    @PostMapping("/users/{userId}/coupon-stores")
    public ResponseEntity<CommonResponse<CouponStoreResponse>> issueCoupon(@RequestBody CouponIssueRequest request) {
        CouponStoreResponse response = CouponStoreResponse.fromEntity(couponStoreService.issueCoupon(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(response));
    }

    /**
     * Retrieves all coupon stores associated with the specified user.
     *
     * @param userId the ID of the user whose coupon stores are to be retrieved
     * @return a response containing a list of coupon store details for the user
     */
    @GetMapping("/users/{userId}/coupon-stores")
    public ResponseEntity<CommonResponse<List<CouponStoreResponse>>> getStoresByUserId(@PathVariable Long userId) {
        List<CouponStore> stores = couponStoreService.getStoresByUserId(userId);
        List<CouponStoreResponse> responses = stores.stream()
                .map(CouponStoreResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(responses));
    }

    /****
     * Retrieves all unused coupon stores for a specified user.
     *
     * @param userId the ID of the user whose unused coupon stores are to be retrieved
     * @return a response containing a list of unused coupon store details for the user
     */
    @GetMapping("/users/{userId}/coupon-stores/status-unused")
    public ResponseEntity<CommonResponse<List<CouponStoreResponse>>> getUnusedStores(@PathVariable Long userId) {
        List<CouponStore> stores = couponStoreService.getUnusedStoresByUserId(userId);
        List<CouponStoreResponse> responses = stores.stream()
                .map(CouponStoreResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(responses));
    }

    /****
     * Retrieves all coupon store entries issued for a specific coupon.
     *
     * @param couponId the ID of the coupon to look up
     * @return a response containing a list of coupon store details associated with the given coupon
     */
    @GetMapping("/coupons/{couponId}/coupon-stores")
    public ResponseEntity<CommonResponse<List<CouponStoreResponse>>> getStoresByCouponId(@PathVariable Long couponId) {
        List<CouponStore> stores = couponStoreService.getStoresByCouponId(couponId);
        List<CouponStoreResponse> responses = stores.stream()
                .map(CouponStoreResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(responses));
    }

    /**
     * Updates the details of a coupon store entry, such as its usage status.
     *
     * @param storeId the ID of the coupon store to update
     * @param request the update request containing new coupon store details
     * @return a response entity containing the updated coupon store information
     */
    @PutMapping("/coupon-stores/{storeId}")
    public ResponseEntity<CommonResponse<CouponStoreUpdateResponse>> updateStore(
            @PathVariable Long storeId,
            @RequestBody CouponStoreUpdateRequest request) {
        CouponStoreUpdateResponse response = CouponStoreUpdateResponse.of(
                couponStoreService.updateStore(storeId, request)
        );
        return ResponseEntity.ok(CommonResponse.update(response));
    }

    /**
     * Deletes a coupon store entry by its ID.
     *
     * Removes the specified coupon store and returns a success response with no content.
     *
     * @param storeId the ID of the coupon store to delete
     * @return a response indicating successful deletion
     */
    @DeleteMapping("/coupon-stores/{storeId}")
    public ResponseEntity<CommonResponse<Void>> deleteStore(@PathVariable Long storeId) {
        couponStoreService.deleteStore(storeId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    /**
     * Retrieves coupons applicable to a user for a specific book and category.
     *
     * Returns a list of coupon responses, each including detailed book or category information if relevant to the coupon's origin.
     *
     * @param userId the ID of the user for whom applicable coupons are retrieved
     * @param bookId the ID of the book to check coupon applicability
     * @param categoryId the ID of the category to check coupon applicability
     * @return a response entity containing a list of applicable coupons with associated book or category details
     */
    @GetMapping("/users/{userId}/applicable-coupons")
    public ResponseEntity<List<CouponResponse>> getApplicableCoupons(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam Long categoryId
    ) {
        List<CouponStore> stores = couponStoreService.getApplicableCouponStores(userId, bookId, categoryId);

        List<CouponResponse> responses = stores.stream()
                .map(store -> {
                    Coupon coupon = store.getCoupon();
                    OriginType originType = store.getOriginType();
                    Long originId = store.getOriginId();

                    List<CouponResponse.BookInfo> books = List.of();
                    List<CouponResponse.CategoryInfo> categories = List.of();

                    if (originType == OriginType.BOOK && originId != null) {
                        BookCoupon bc = bookCouponRepository.getReferenceById(originId);
                        books = List.of(new CouponResponse.BookInfo(
                                originId,
                                bc.getBook().getId(),
                                bc.getBook().getTitle()
                        ));
                    }

                    if (originType == OriginType.CATEGORY && originId != null) {
                        CategoryCoupon cc = categoryCouponRepository.getReferenceById(originId);
                        categories = List.of(new CouponResponse.CategoryInfo(
                                originId,
                                cc.getCategory().getId(),
                                cc.getCategory().getName()
                        ));
                    }

                    return CouponResponse.from(coupon, books, categories);
                })
                .toList();

        return ResponseEntity.ok(responses);
    }


}

