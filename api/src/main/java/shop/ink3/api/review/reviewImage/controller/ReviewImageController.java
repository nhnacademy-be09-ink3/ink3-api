package shop.ink3.api.review.reviewImage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.ink3.api.review.reviewImage.service.ReviewImageService;

@RestController
@RequestMapping("/reviews/images")
@RequiredArgsConstructor
public class ReviewImageController {

    private final ReviewImageService reviewImageService;

    @GetMapping("/upload-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String fileName) {
        // objectKey: reviews/{timestamp}_{fileName}
        String objectKey = "reviews/" + System.currentTimeMillis() + "_" + fileName;
        String url = reviewImageService.generateUploadUrl(objectKey);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/download-url")
    public ResponseEntity<String> getPresignedDownloadUrl(@RequestParam String objectKey) {
        String url = reviewImageService.generateDownloadUrl(objectKey);
        return ResponseEntity.ok(url);
    }
}
