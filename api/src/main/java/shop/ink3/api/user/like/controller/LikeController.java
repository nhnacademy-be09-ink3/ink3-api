package shop.ink3.api.user.like.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.like.dto.LikeCreateRequest;
import shop.ink3.api.user.like.dto.LikeResponse;
import shop.ink3.api.user.like.service.LikeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/likes")
public class LikeController {
    private final LikeService likeService;

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<LikeResponse>>> getLikes(
            @PathVariable long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(CommonResponse.success(likeService.getLikes(userId, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<LikeResponse>> createLike(
            @PathVariable long userId,
            @RequestBody @Valid LikeCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(likeService.createLike(userId, request)));
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable long userId, @PathVariable long likeId) {
        likeService.deleteLike(userId, likeId);
        return ResponseEntity.noContent().build();
    }
}
