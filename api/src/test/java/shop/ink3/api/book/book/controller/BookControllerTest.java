package shop.ink3.api.book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookSearchRequest;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.exception.InvalidCategorySelectionException;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.common.dto.PageResponse;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookResponse createSampleBookResponse() {
        return new BookResponse(
                1L,
                "isbn",
                "title",
                "toc",
                "desc",
                "publisher",
                LocalDate.of(2024, 5, 16),
                20000,
                18000,
                10,
                10,
                BookStatus.AVAILABLE,
                true,
                "thumb",
                List.of("카테고리1", "카테고리2"),
                List.of("작가1"),
                List.of("태그1")
        );
    }

//    @Test
//    void createBook_shouldReturnCreatedBook() throws Exception {
//        BookCreateRequest request = new BookCreateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.of(2024, 5, 16),
//                20000, 18000, 10, BookStatus.AVAILABLE, true,
//                "thumb", 1L, List.of(1L, 2L), List.of(1L), List.of(1L)
//        );
//
//        given(bookService.createBook(any())).willReturn(createSampleBookResponse());
//
//        mockMvc.perform(post("/api/books/books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.title").value("title"));
//    }

//    @Test
//    void getBooksByTitle_shouldReturnList() throws Exception {
//        given(bookService.findAllByTitle("title")).willReturn(List.of(createSampleBookResponse()));
//
//        mockMvc.perform(get("/api/books/books/title/title"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("title"));
//    }
//
//    @Test
//    void getBooksByAuthor_shouldReturnList() throws Exception {
//        given(bookService.findAllByAuthor("author")).willReturn(List.of(createSampleBookResponse()));
//
//        mockMvc.perform(get("/api/books/books/author/author"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].authorNames[0]").value("작가1"));
//    }

//    @Test
//    void getBooks_shouldReturnAllBooks() throws Exception {
//        given(bookService.findAllByAuthor("")).willReturn(List.of(createSampleBookResponse()));
//
//        mockMvc.perform(get("/api/books/books"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("title"));
//    }

//    @Test
//    void updateBook_shouldReturnUpdatedBook() throws Exception {
//        BookUpdateRequest request = new BookUpdateRequest(
//                "isbn", "updated title", "toc", "desc", LocalDate.of(2025, 5, 16),
//                25000, 20000, 5, BookStatus.AVAILABLE, false,
//                "thumb", 1L, List.of(1L, 2L), List.of(1L), List.of(1L)
//        );
//
//        given(bookService.updateBook(eq(1L), any())).willReturn(createSampleBookResponse());
//
//        mockMvc.perform(put("/api/books/books/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.title").value("title"));
//    }

//    @Test
//    void deleteBook_shouldReturnNoContent() throws Exception {
//        mockMvc.perform(delete("/api/books/books/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void registerBookByIsbn_shouldReturnBook() throws Exception {
//        given(bookService.registerBookByIsbn("isbn")).willReturn(createSampleBookResponse());
//
//        mockMvc.perform(post("/api/books/books/register-by-isbn?isbn=isbn"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.title").value("title"));
//    }
//
//    @Test
//    void searchBooks_shouldReturnPagedBooks() throws Exception {
//        PageResponse<BookResponse> pageResponse = new PageResponse<>(
//                List.of(createSampleBookResponse()),
//                0, 10, 1L, 1, false, false
//        );
//
//        given(bookService.searchBooks(any(BookSearchRequest.class))).willReturn(pageResponse);
//
//        mockMvc.perform(get("/api/books/books/search?page=0&size=10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].title").value("title"));
//    }

//    @Test
//    void getBookById_shouldReturnBook() throws Exception {
//        given(bookService.getBook(1L)).willReturn(createSampleBookResponse());
//
//        mockMvc.perform(get("/api/books/books/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("title"));
//    }
//
//    @Test
//    void getBookById_shouldReturnNotFound_whenNotExists() throws Exception {
//        given(bookService.getBook(999L)).willReturn(null);
//
//        mockMvc.perform(get("/api/books/books/999"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(""));
//    }

//    @Test
//    void updateBook_shouldReturnNotFound_whenBookNotExists() throws Exception {
//        BookUpdateRequest request = new BookUpdateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.now(),
//                10000, 9000, 10, BookStatus.AVAILABLE,
//                true, "thumb", 1L, List.of(1L), List.of(1L), List.of(1L)
//        );
//
//        given(bookService.updateBook(eq(999L), any())).willThrow(new RuntimeException("Book not found"));
//
//        mockMvc.perform(put("/api/books/books/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isInternalServerError()); // 예외처리 미구현 시 500
//    }

//    @Test
//    void createBook_shouldAcceptMaxCategoryLimit() throws Exception {
//        List<Long> tenCategories = List.of(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L);
//
//        BookCreateRequest request = new BookCreateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.now(),
//                10000, 9000, 10, BookStatus.AVAILABLE,
//                true, "thumb", 1L, tenCategories, List.of(1L), List.of(1L)
//        );
//
//        given(bookService.createBook(any())).willReturn(createSampleBookResponse());
//
//        mockMvc.perform(post("/api/books/books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated());
//    }

//    @Test
//    void createBook_shouldReturnInternalServerError_whenTooManyCategories() throws Exception {
//        List<Long> elevenCategories = List.of(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L);
//
//        BookCreateRequest request = new BookCreateRequest(
//                "isbn", "title", "toc", "desc", LocalDate.now(),
//                10000, 9000, 10, BookStatus.AVAILABLE,
//                true, "thumb", 1L, elevenCategories, List.of(1L), List.of(1L)
//        );
//
//        given(bookService.createBook(any()))
//                .willThrow(new InvalidCategorySelectionException("카테고리는 최대 10개까지만 선택할 수 있습니다."));
//
//        mockMvc.perform(post("/api/books/books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("카테고리는 최대 10개까지만 선택할 수 있습니다."));
//    }
}