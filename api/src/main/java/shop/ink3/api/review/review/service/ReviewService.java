package shop.ink3.api.review.review.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.common.uploader.MinioUploader;
import shop.ink3.api.common.util.PresignUrlPrefixUtil;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.orderBook.exception.OrderBookNotFoundException;
import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
import shop.ink3.api.review.review.dto.ReviewDefaultListResponse;
import shop.ink3.api.review.review.dto.ReviewListResponse;
import shop.ink3.api.review.review.dto.ReviewRequest;
import shop.ink3.api.review.review.dto.ReviewResponse;
import shop.ink3.api.review.review.dto.ReviewUpdateRequest;
import shop.ink3.api.review.review.entity.Review;
import shop.ink3.api.review.review.exception.ReviewAlreadyRegisterException;
import shop.ink3.api.review.review.exception.ReviewNotFoundException;
import shop.ink3.api.review.review.exception.UnauthorizedOrderBookAccessException;
import shop.ink3.api.review.review.repository.ReviewRepository;
import shop.ink3.api.review.reviewImage.dto.ReviewImageMapping;
import shop.ink3.api.review.reviewImage.dto.ReviewImageResponse;
import shop.ink3.api.review.reviewImage.entity.ReviewImage;
import shop.ink3.api.review.reviewImage.repository.ReviewImageRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final UserRepository userRepository;
    private final OrderBookRepository orderBookRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MinioUploader minioUploader;
    private final PresignUrlPrefixUtil presignUrlPrefixUtil;

    @Value("${minio.review-bucket}")
    private String bucket;

    public ReviewResponse addReview(ReviewRequest request, List<MultipartFile> images) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));
        OrderBook orderBook = orderBookRepository.findById(request.orderBookId())
            .orElseThrow(() -> new OrderBookNotFoundException(request.orderBookId()));

        if (!orderBook.getOrder().getUser().getId().equals(request.userId())) {
            throw new UnauthorizedOrderBookAccessException(user.getId(), orderBook.getId());
        }

        if (reviewRepository.existsByOrderBookId(orderBook.getId())) {
            throw new ReviewAlreadyRegisterException(orderBook.getId());
        }

        Review review = Review.builder()
            .user(user)
            .orderBook(orderBook)
            .title(request.title())
            .content(request.content())
            .rating(request.rating())
            .build();
        Review savedReview = reviewRepository.save(review);

        List<String> imageUrls = saveImages(images, savedReview);
        log.warn("ReviewService===========imageUrls={}", Arrays.toString(imageUrls.toArray()));

        return ReviewResponse.from(savedReview, imageUrls);
    }

    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request, List<MultipartFile> images, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedOrderBookAccessException(userId, review.getOrderBook().getId());
        }

        review.update(request.getTitle(), request.getContent(), request.getRating());

        List<String> imageUrls;
        if (images != null && images.stream().anyMatch(image -> !image.isEmpty())) {
            List<ReviewImage> existingImages = reviewImageRepository.findByReviewId(review.getId());
            for (ReviewImage image : existingImages) {
                minioUploader.delete(image.getImageUrl(), bucket);
                reviewImageRepository.deleteById(image.getId());
            }

            imageUrls = saveImages(images, review);
        } else {
            imageUrls = reviewImageRepository.findByReviewId(review.getId()).stream()
                .map(ReviewImage::getImageUrl)
                .map(url -> minioUploader.getPresignedUrl(url, bucket))
                .toList();
        }

        return ReviewResponse.from(review, imageUrls);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewListResponse> getReviewsByUserId(Pageable pageable, Long userId) {
        Page<ReviewDefaultListResponse> page = reviewRepository.findListByUserId(pageable, userId);
        return getReviewListResponsePage(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewListResponse> getReviewsByBookId(Pageable pageable, Long bookId) {
        Page<ReviewDefaultListResponse> page = reviewRepository.findListByBookId(pageable, bookId);
        return getReviewListResponsePage(page);
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        List<ReviewImage> images = reviewImageRepository.findByReviewId(review.getId());

        for (ReviewImage image : images) {
            minioUploader.delete(image.getImageUrl(), bucket);
            reviewImageRepository.deleteById(image.getId());
        }

        reviewRepository.deleteById(reviewId);
    }

    private List<String> saveImages(List<MultipartFile> images, Review review) {
        if (images == null || images.isEmpty()) return List.of();

        return images.stream()
            .map(image -> {
                String imageUrl = minioUploader.upload(image, bucket);
                reviewImageRepository.save(
                    ReviewImage.builder()
                        .review(review)
                        .imageUrl(imageUrl)
                        .build()
                );
                return imageUrl;
            })
            .toList();
    }

    private PageResponse<ReviewListResponse> getReviewListResponsePage(Page<ReviewDefaultListResponse> page) {
        List<Long> reviewIds = page.getContent().stream()
            .map(ReviewDefaultListResponse::id)
            .toList();

        Map<Long, List<String>> imageMap = reviewImageRepository.findByReviewIdIn(reviewIds).stream()
            .map(image -> new ReviewImageMapping(image.getReview().getId(), image.getImageUrl()))
            .collect(Collectors.groupingBy(
                ReviewImageMapping::reviewId,
                Collectors.mapping(ReviewImageMapping::imageUrl, Collectors.toList())
            ));

        Page<ReviewListResponse> mappedPage = page.map(dto -> {
            List<ReviewImageResponse> images = imageMap.getOrDefault(dto.id(), List.of()).stream()
                .map(url -> {
                    String presignedUrl = minioUploader.getPresignedUrl(url, bucket);
                    return new ReviewImageResponse(presignUrlPrefixUtil.addPrefixUrl(presignedUrl));
                })
                .toList();

            return new ReviewListResponse(
                dto.id(),
                dto.userId(),
                dto.orderBookId(),
                dto.userName(),
                dto.title(),
                dto.content(),
                dto.rating(),
                dto.createdAt(),
                dto.modifiedAt(),
                images
            );
        });

        return PageResponse.from(mappedPage);
    }
}
