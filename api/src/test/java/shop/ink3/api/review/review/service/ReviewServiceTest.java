// package shop.ink3.api.review.review.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.web.multipart.MultipartFile;
//
// import shop.ink3.api.book.book.entity.Book;
// import shop.ink3.api.common.uploader.MinioUploader;
// import shop.ink3.api.order.orderBook.entity.OrderBook;
// import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
// import shop.ink3.api.review.review.dto.ReviewDefaultListResponse;
// import shop.ink3.api.review.review.dto.ReviewRequest;
// import shop.ink3.api.review.review.dto.ReviewResponse;
// import shop.ink3.api.review.review.dto.ReviewUpdateRequest;
// import shop.ink3.api.review.review.entity.Review;
// import shop.ink3.api.review.review.exception.ReviewNotFoundException;
// import shop.ink3.api.review.review.repository.ReviewRepository;
// import shop.ink3.api.review.reviewImage.entity.ReviewImage;
// import shop.ink3.api.review.reviewImage.repository.ReviewImageRepository;
// import shop.ink3.api.user.user.entity.User;
// import shop.ink3.api.user.user.repository.UserRepository;
//
// class ReviewServiceTest {
//
//     @Mock private UserRepository userRepository;
//     @Mock private OrderBookRepository orderBookRepository;
//     @Mock private ReviewRepository reviewRepository;
//     @Mock private ReviewImageRepository reviewImageRepository;
//     @Mock private MinioUploader minioUploader;
//
//     @InjectMocks
//     private ReviewService reviewService;
//
//     private User user;
//     private OrderBook orderBook;
//
//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         user = User.builder().id(1L).name("test").build();
//         orderBook = OrderBook.builder().id(1L).book(Book.builder().id(1L).build()).build();
//         ReflectionTestUtils.setField(reviewService, "bucket", "test-bucket");
//     }
//
//     @Test
//     @DisplayName("리뷰 등록")
//     void addReview() {
//         ReviewRequest request = new ReviewRequest(1L, 1L, "title", "content", 5);
//         Review review = new Review(user, orderBook, "title", "content", 5);
//         ReflectionTestUtils.setField(review, "id", 1L);
//
//         when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//         when(orderBookRepository.findById(1L)).thenReturn(Optional.of(orderBook));
//         when(reviewRepository.save(any())).thenReturn(review);
//         when(reviewImageRepository.findByReviewId(1L)).thenReturn(List.of());
//
//         ReviewResponse response = reviewService.addReview(request, null);
//
//         assertThat(response.title()).isEqualTo("title");
//         assertThat(response.images()).isEmpty();
//     }
//
//     @Test
//     @DisplayName("리뷰 수정")
//     void updateReview() {
//         Review review = new Review(user, orderBook, "old title", "old content", 3);
//         ReflectionTestUtils.setField(review, "id", 1L);
//
//         ReviewUpdateRequest request = new ReviewUpdateRequest("new title", "new content", 4);
//
//         when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//         when(reviewImageRepository.findByReviewId(1L)).thenReturn(List.of());
//         when(minioUploader.upload(any(MultipartFile.class), anyString(), anyString())).thenReturn("new-image.jpg");
//
//         ReviewResponse response = reviewService.updateReview(1L, request, List.of(mock(MultipartFile.class)));
//
//         assertThat(response.title()).isEqualTo("new title");
//         assertThat(response.rating()).isEqualTo(4);
//     }
//
//     @Test
//     @DisplayName("리뷰 삭제")
//     void deleteReview() {
//         Review review = new Review(user, orderBook, "title", "content", 5);
//         ReflectionTestUtils.setField(review, "id", 1L);
//
//         when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//         when(reviewImageRepository.findByReviewId(1L)).thenReturn(List.of());
//
//         reviewService.deleteReview(1L);
//
//         verify(reviewRepository).deleteById(1L);
//     }
//
//     @Test
//     @DisplayName("리뷰 단건 조회")
//     void getReviewByUserId() {
//         Review review = new Review(user, orderBook, "title", "content", 5);
//         ReflectionTestUtils.setField(review, "id", 1L);
//
//         when(reviewRepository.findByUserId(1L)).thenReturn(review);
//         when(reviewImageRepository.findByReviewId(1L)).thenReturn(List.of(ReviewImage.builder().imageUrl("img1.jpg").build()));
//
//         ReviewResponse response = reviewService.getReviewByUserId(1L);
//
//         assertThat(response.title()).isEqualTo("title");
//         assertThat(response.images()).hasSize(1);
//     }
//
//     @Test
//     @DisplayName("리뷰 전체 조회")
//     void getReviewsByBookId() {
//         ReviewDefaultListResponse dto = new ReviewDefaultListResponse(1L, 1L, 1L, "user", "title", "content", 5, null, null);
//         Page<ReviewDefaultListResponse> page = new PageImpl<>(List.of(dto));
//
//         Review review = new Review(user, orderBook, "title", "content", 5);
//         ReflectionTestUtils.setField(review, "id", 1L);
//
//         ReviewImage image = ReviewImage.builder().imageUrl("img1.jpg").review(review).build();
//
//         when(reviewRepository.findListByBookId(any(), anyLong())).thenReturn(page);
//         when(reviewImageRepository.findByReviewIdIn(List.of(1L))).thenReturn(List.of(image));
//
//         var response = reviewService.getReviewsByBookId(PageRequest.of(0, 10), 1L);
//
//         assertThat(response.content()).hasSize(1);
//         assertThat(response.content().get(0).images()).hasSize(1);
//     }
//
//     @Test
//     @DisplayName("리뷰 수정 - 예외")
//     void updateReviewFail() {
//         when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
//
//         assertThrows(ReviewNotFoundException.class,
//             () -> reviewService.updateReview(1L, new ReviewUpdateRequest("t", "c", 3), null));
//     }
//
//     @Test
//     @DisplayName("리뷰 삭제 - 예외")
//     void deleteReviewFail() {
//         when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
//
//         assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(1L));
//     }
// }
