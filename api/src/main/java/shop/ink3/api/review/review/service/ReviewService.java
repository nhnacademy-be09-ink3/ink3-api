package shop.ink3.api.review.review.service;

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
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.common.uploader.MinioUploader;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.orderBook.exception.OrderBookNotFoundException;
import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
import shop.ink3.api.review.review.dto.ReviewDefaultListResponse;
import shop.ink3.api.review.reviewImage.dto.ReviewImageResponse;
import shop.ink3.api.review.review.dto.ReviewListResponse;
import shop.ink3.api.review.review.dto.ReviewRequest;
import shop.ink3.api.review.review.dto.ReviewResponse;
import shop.ink3.api.review.review.dto.ReviewUpdateRequest;
import shop.ink3.api.review.review.entity.Review;
import shop.ink3.api.review.review.exception.ReviewNotFoundException;
import shop.ink3.api.review.review.repository.ReviewRepository;
import shop.ink3.api.review.reviewImage.entity.ReviewImage;
import shop.ink3.api.review.reviewImage.repository.ReviewImageRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final UserRepository userRepository;
    private final OrderBookRepository orderBookRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MinioUploader minioUploader;

    @Value("${minio.review-bucket}")
    private String bucket;

    public ReviewResponse addReview(ReviewRequest request, List<MultipartFile> images) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));
        OrderBook orderBook = orderBookRepository.findById(request.orderBookId())
            .orElseThrow(() -> new OrderBookNotFoundException(request.orderBookId()));

        Review review = Review.builder()
            .user(user)
            .orderBook(orderBook)
            .title(request.title())
            .content(request.content())
            .rating(request.rating())
            .build();
        Review savedReview = reviewRepository.save(review);

        List<String> imageUrls = saveImages(images, savedReview);

        return ReviewResponse.from(savedReview, imageUrls);
    }

    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request, List<MultipartFile> images) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        review.update(request.title(), request.content(), request.rating());

        reviewImageRepository.findByReviewId(review.getId()).forEach(image -> reviewImageRepository.deleteById(image.getId()));
        saveImages(images, review);

        List<String> imageUrls = saveImages(images, review);

        return ReviewResponse.from(review, imageUrls);
    }

    public ReviewResponse getReviewByUserId(Long userId) {
        Review review = reviewRepository.findByUserId(userId);
        List<String> imageUrls = reviewImageRepository.findByReviewId(review.getId()).stream()
            .map(ReviewImage::getImageUrl)
            .collect(Collectors.toList());
        return ReviewResponse.from(review, imageUrls);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewListResponse> getReviewsByBookId(Pageable pageable, Long bookId) {
        Page<ReviewDefaultListResponse> page = reviewRepository.findListByBookId(pageable, bookId);

        List<Long> reviewIds = page.getContent().stream()
            .map(ReviewDefaultListResponse::id)
            .toList();

        Map<Long, List<ReviewImage>> imageMap = reviewImageRepository.findByReviewIdIn(reviewIds).stream()
            .collect(Collectors.groupingBy(image -> image.getReview().getId()));

        Page<ReviewListResponse> mappedPage = page.map(dto -> {
            List<ReviewImageResponse> images = imageMap.getOrDefault(dto.id(), List.of()).stream()
                .map(ReviewImageResponse::from)
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

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new ReviewNotFoundException(id));

        reviewImageRepository.findByReviewId(review.getId())
            .forEach(image -> reviewImageRepository.deleteById(image.getId()));

        reviewRepository.deleteById(id);
    }

    private List<String> saveImages(List<MultipartFile> images, Review review) {
        if (images == null || images.isEmpty()) return List.of();

        return images.stream()
            .map(image -> {
                String imageUrl = minioUploader.upload(image, bucket, "reviews");
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
}
