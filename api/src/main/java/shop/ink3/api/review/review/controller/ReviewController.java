package shop.ink3.api.review.review.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.review.review.dto.ReviewRequest;
import shop.ink3.api.review.review.dto.ReviewResponse;
import shop.ink3.api.review.review.dto.ReviewUpdateRequest;
import shop.ink3.api.review.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<CommonResponse<ReviewResponse>> addReview(@RequestBody @Valid ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(reviewService.addReview(request)));
    }

    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<CommonResponse<ReviewResponse>> getReviewByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(CommonResponse.success(reviewService.getReviewByUserId(userId)));
    }

    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByBookId(
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(pageable, bookId));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewResponse>> updateReview(@PathVariable Long reviewId,
        @RequestBody @Valid ReviewUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(reviewService.updateReview(reviewId, request)));
    }


    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
