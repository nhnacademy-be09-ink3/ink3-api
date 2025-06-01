// package shop.ink3.api.review.review.controller;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.time.LocalDateTime;
// import java.util.List;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.http.MediaType;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import shop.ink3.api.common.dto.PageResponse;
// import shop.ink3.api.review.review.dto.ReviewListResponse;
// import shop.ink3.api.review.review.dto.ReviewRequest;
// import shop.ink3.api.review.review.dto.ReviewResponse;
// import shop.ink3.api.review.review.dto.ReviewUpdateRequest;
// import shop.ink3.api.review.review.service.ReviewService;
// import shop.ink3.api.review.reviewImage.dto.ReviewImageResponse;
//
// @WebMvcTest(ReviewController.class)
// class ReviewControllerTest {
//
//     @Autowired
//     private MockMvc mockMvc;
//
//     @MockitoBean
//     private ReviewService reviewService;
//
//     @Autowired
//     private ObjectMapper objectMapper;
//
//     private ReviewRequest reviewRequest;
//     private ReviewUpdateRequest reviewUpdateRequest;
//     private ReviewResponse reviewResponse;
//     private ReviewListResponse reviewListResponse;
//
//     @BeforeEach
//     void setUp() {
//         reviewRequest = new ReviewRequest(1L, 1L, "title", "content", 5);
//         reviewUpdateRequest = new ReviewUpdateRequest("updatedTitle", "updatedContent", 4);
//         reviewResponse = new ReviewResponse(1L, 1L, 1L, "title", "content", 5,
//             LocalDateTime.now(), LocalDateTime.now(),
//             List.of("image1.jpg", "image2.jpg"));
//
//         reviewListResponse = new ReviewListResponse(1L, 1L, 1L, "user1", "title", "content", 5,
//             LocalDateTime.now(), LocalDateTime.now(),
//             List.of(new ReviewImageResponse("image1.jpg"), new ReviewImageResponse("image2.jpg")));
//     }
//
//     @Test
//     @DisplayName("리뷰 등록")
//     void addReview() throws Exception {
//         MockMultipartFile file = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "test".getBytes());
//         MockMultipartFile json = new MockMultipartFile("review", "", "application/json",
//             objectMapper.writeValueAsBytes(reviewRequest));
//
//         when(reviewService.addReview(any(), any())).thenReturn(reviewResponse);
//
//         mockMvc.perform(multipart("/reviews")
//                 .file(file)
//                 .file(json)
//                 .contentType(MediaType.MULTIPART_FORM_DATA))
//             .andExpect(status().isCreated())
//             .andExpect(jsonPath("$.data.title").value("title"))
//             .andExpect(jsonPath("$.data.images.length()").value(2))
//             .andExpect(jsonPath("$.data.images[0]").value("image1.jpg"));
//     }
//
//     @Test
//     @DisplayName("리뷰 수정")
//     void updateReview() throws Exception {
//         ReviewResponse updatedResponse = new ReviewResponse(1L, 1L, 1L, "updatedTitle", "updatedContent", 4,
//             LocalDateTime.now(), LocalDateTime.now(),
//             List.of("image3.jpg", "image4.jpg"));
//
//         MockMultipartFile file = new MockMultipartFile("images", "image3.jpg", "image/jpeg", "test".getBytes());
//         MockMultipartFile json = new MockMultipartFile("review", "", "application/json",
//             objectMapper.writeValueAsBytes(reviewUpdateRequest));
//
//         when(reviewService.updateReview(eq(1L), any(), any())).thenReturn(updatedResponse);
//
//         mockMvc.perform(multipart("/reviews/1")
//                 .file(file)
//                 .file(json)
//                 .with(req -> {
//                     req.setMethod("PUT");
//                     return req;
//                 })
//                 .contentType(MediaType.MULTIPART_FORM_DATA))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.data.title").value("updatedTitle"))
//             .andExpect(jsonPath("$.data.images[0]").value("image3.jpg"));
//     }
//
//     @Test
//     @DisplayName("유저의 리뷰 단건 조회")
//     void getReviewByUserId() throws Exception {
//         when(reviewService.getReviewByUserId(1L)).thenReturn(reviewResponse);
//
//         mockMvc.perform(get("/users/1/reviews"))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.data.title").value("title"))
//             .andExpect(jsonPath("$.data.images[0]").value("image1.jpg"));
//     }
//
//     @Test
//     @DisplayName("도서 ID로 리뷰 목록 조회")
//     void getReviewsByBookId() throws Exception {
//         PageResponse<ReviewListResponse> pageResponse = PageResponse.from(
//             new PageImpl<>(List.of(reviewListResponse), PageRequest.of(0, 10), 1)
//         );
//
//         when(reviewService.getReviewsByBookId(any(), eq(1L))).thenReturn(pageResponse);
//
//         mockMvc.perform(get("/books/1/reviews?page=0&size=10&sort=createdAt,DESC"))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.content[0].title").value("title"))
//             .andExpect(jsonPath("$.content[0].images[0].imageUrl").value("image1.jpg"));
//     }
//
//     @Test
//     @DisplayName("리뷰 삭제")
//     void deleteReview() throws Exception {
//         doNothing().when(reviewService).deleteReview(1L);
//
//         mockMvc.perform(delete("/reviews/1"))
//             .andExpect(status().isNoContent());
//     }
// }
