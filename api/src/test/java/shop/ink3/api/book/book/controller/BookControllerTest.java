package shop.ink3.api.book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.service.BookService;

import java.time.LocalDate;
import java.util.List;
import shop.ink3.api.common.dto.PageResponse;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void addBook_shouldReturnCreatedBook() throws Exception {
        BookCreateRequest request = new BookCreateRequest(
                "1234567890123", "테스트책", "목차", "설명", LocalDate.now(),
                20000, 15000, 100, BookStatus.AVAILABLE, true, "url",
                1L, List.of(1L), List.of(1L), List.of(1L)
        );
        BookResponse response = new BookResponse(1L, "테스트책", "설명", "목차", "1234567890123", "url",
                20000, 15000, true, LocalDate.now(), "출판사", List.of("저자"), List.of("카테고리"), List.of("태그"));

        given(bookService.save(any())).willReturn(response);

        mockMvc.perform(post("/api/books/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트책"));
    }

    @Test
    void registerByIsbn_shouldReturnBook() throws Exception {
        BookResponse response = new BookResponse(1L, "알라딘책", "desc", "toc", "9876543210987", "url",
                25000, 20000, true, LocalDate.now(), "출판사", List.of("저자"), List.of("카테고리"), List.of("태그"));
        given(bookService.registerBookByIsbn("9876543210987")).willReturn(response);

        mockMvc.perform(post("/api/books/books/register-by-isbn")
                        .param("isbn", "9876543210987"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ISBN").value("9876543210987"));
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        BookResponse response = new BookResponse(1L, "단건조회", "desc", "toc", "1111111111111", "url",
                12000, 10000, true, LocalDate.now(), "출판사", List.of("저자"), List.of("카테고리"), List.of("태그"));
        given(bookService.findById(1L)).willReturn(response);

        mockMvc.perform(get("/api/books/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("단건조회"));
    }

    @Test
    void searchBooks_shouldReturnList() throws Exception {
        BookResponse book = new BookResponse(1L, "제목검색", "desc", "toc", "ISBN", "url",
                15000, 10000, true, LocalDate.now(), "출판사", List.of("저자"), List.of("카테고리"), List.of("태그"));

        PageResponse<BookResponse> page = PageResponse.of(List.of(book), 0, 10, 1);
        given(bookService.searchBooks(any())).willReturn(page);

        mockMvc.perform(get("/api/books/books/search")
                        .param("title", "제목"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("제목검색"));
    }

    @Test
    void getBooksByAuthor_shouldReturnBooks() throws Exception {
        BookResponse book = new BookResponse(1L, "저자책", "desc", "toc", "ISBN", "url",
                15000, 10000, true, LocalDate.now(), "출판사", List.of("저자"), List.of("카테고리"), List.of("태그"));
        given(bookService.findAllByAuthor("홍길동")).willReturn(List.of(book));

        mockMvc.perform(get("/api/books/books/author/홍길동"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("저자책"));
    }

    @Test
    void getBooksByTitle_shouldReturnBooks() throws Exception {
        BookResponse book = new BookResponse(1L, "제목책", "desc", "toc", "ISBN", "url",
                15000, 10000, true, LocalDate.now(), "출판사", List.of("저자"), List.of("카테고리"), List.of("태그"));
        given(bookService.findAllByTitle("제목책")).willReturn(List.of(book));

        mockMvc.perform(get("/api/books/books/title/제목책"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("제목책"));
    }
    @Test
    void getBooks_shouldReturnAllBooks() throws Exception {
        BookResponse book = new BookResponse(1L, "전체조회책", "desc", "toc", "ISBN", "url",
                15000, 10000, true, LocalDate.now(), "출판사", List.of("저자"), List.of("카테고리"), List.of("태그"));

        given(bookService.findAllByAuthor("")).willReturn(List.of(book));

        mockMvc.perform(get("/api/books/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("전체조회책"));
    }
}