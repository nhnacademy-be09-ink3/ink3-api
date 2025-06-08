package shop.ink3.api.book.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.book.book.dto.AuthorDto;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.MainBookResponse;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.enums.SortType;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.tag.dto.TagResponse;
import shop.ink3.api.common.dto.PageResponse;

@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private BookResponse bookResponse;
    private MainBookResponse mainBookResponse;

    @BeforeEach
    void setUp() {
        CategoryResponse category = new CategoryResponse(
            3L, "한국소설", 1L, List.of()
        );
        List<CategoryResponse> categories = List.of(category);

        AuthorDto author = new AuthorDto(
            100L, "홍길동", "저자"
        );
        List<AuthorDto> authors = List.of(author);

        TagResponse tag = new TagResponse(
            50L, "베스트셀러"
        );
        List<TagResponse> tags = List.of(tag);

        this.bookResponse = new BookResponse(
            1L,                             // id
            "1234567890123",                // isbn
            "책 제목",                        // title
            "책 내용 요약",                   // contents
            "상세 설명",                      // description
            "출판사",                          // publisherName
            LocalDate.of(2024, 1, 1),       // publishedAt
            20000,                          // originalPrice
            18000,                          // salePrice
            10,                             // discountRate
            100,                            // quantity
            BookStatus.AVAILABLE,           // status
            true,                           // isPackable
            "https://example.com/image.jpg", // thumbnailUrl
            categories,                     // List<CategoryResponse>
            authors,                        // List<AuthorDto>
            tags,                           // List<TagResponse>
            4.5                             // ⭐️ averageRating 추가
        );

        this.mainBookResponse = new MainBookResponse(
            1L, "책 제목", 20000, 18000, 10,
            "https://example.com/image.jpg",
            true,
            List.of("홍길동 (저자)"), 5, 5
        );
    }

    @Test
    @DisplayName("전체 도서 목록 조회")
    void getBooks() throws Exception {
        PageResponse<BookResponse> pageResponse = PageResponse.from(new PageImpl<>(List.of(bookResponse)));
        when(bookService.getBooks(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].title").value("책 제목"));
    }

    @Test
    @DisplayName("Top5 베스트셀러 조회")
    void getTop5BestsellerBooks() throws Exception {
        PageResponse<MainBookResponse> response = PageResponse.from(new PageImpl<>(List.of(mainBookResponse)));
        when(bookService.getTop5BestSellerBooks()).thenReturn(response);

        mockMvc.perform(get("/books/bestseller"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].title").value("책 제목"));
    }

    @Test
    @DisplayName("Top5 신간 조회")
    void getTop5NewBooks() throws Exception {
        PageResponse<MainBookResponse> response = PageResponse.from(new PageImpl<>(List.of(mainBookResponse)));
        when(bookService.getTop5NewBooks()).thenReturn(response);

        mockMvc.perform(get("/books/new"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].title").value("책 제목"));
    }

    @Test
    @DisplayName("Top5 추천도서 조회")
    void getTop5RecommendedBooks() throws Exception {
        PageResponse<MainBookResponse> response = PageResponse.from(new PageImpl<>(List.of(mainBookResponse)));
        when(bookService.getTop5RecommendedBooks()).thenReturn(response);

        mockMvc.perform(get("/books/recommend"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].title").value("책 제목"));
    }

    @Test
    @DisplayName("전체 베스트셀러 조회")
    void getAllBestsellerBooks() throws Exception {
        PageResponse<MainBookResponse> response = PageResponse.from(new PageImpl<>(List.of(mainBookResponse)));
        when(bookService.getAllBestSellerBooks(eq(SortType.REVIEW), any())).thenReturn(response);

        mockMvc.perform(get("/books/bestseller-all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].title").value("책 제목"));
    }

    @Test
    @DisplayName("전체 신간 조회")
    void getAllNewBooks() throws Exception {
        PageResponse<MainBookResponse> response = PageResponse.from(new PageImpl<>(List.of(mainBookResponse)));
        when(bookService.getAllNewBooks(eq(SortType.REVIEW), any())).thenReturn(response);

        mockMvc.perform(get("/books/new-all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].title").value("책 제목"));
    }

    @Test
    @DisplayName("전체 추천도서 조회")
    void getAllRecommendedBooks() throws Exception {
        PageResponse<MainBookResponse> response = PageResponse.from(new PageImpl<>(List.of(mainBookResponse)));
        when(bookService.getAllRecommendedBooks(eq(SortType.REVIEW), any())).thenReturn(response);

        mockMvc.perform(get("/books/recommend-all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].title").value("책 제목"));
    }
}
