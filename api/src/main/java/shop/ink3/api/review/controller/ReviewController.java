package shop.ink3.api.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.review.dto.ReviewRequest;
import shop.ink3.api.review.dto.ReviewResponse;
import shop.ink3.api.review.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ReviewResponse> getReviewByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewByUserId(userId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByBookId(
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(pageable, bookId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
