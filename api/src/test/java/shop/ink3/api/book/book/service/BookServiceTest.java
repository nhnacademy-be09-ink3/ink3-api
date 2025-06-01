package shop.ink3.api.book.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.MainBookResponse;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.common.dto.PageResponse;

class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        book = Book.builder()
            .id(1L)
            .isbn("1234567890123")
            .title("책 제목")
            .contents("내용")
            .description("설명")
            .publishedAt(LocalDate.of(2024, 1, 1))
            .originalPrice(20000)
            .salePrice(18000)
            .discountRate(10)
            .status(BookStatus.AVAILABLE)
            .isPackable(true)
            .thumbnailUrl("https://example.com/image.jpg")
            .quantity(100)
            .build();
    }

    @Test
    @DisplayName("도서 단건 조회 성공")
    void getBookSuccess() {
        when(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(book));
        BookResponse result = bookService.getBook(1L);
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("책 제목");
    }

    @Test
    @DisplayName("전체 도서 조회 성공")
    void getBooksSuccess() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);
        PageResponse<BookResponse> response = bookService.getBooks(PageRequest.of(0, 10));
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).title()).isEqualTo("책 제목");
    }

    @Test
    @DisplayName("Top5 베스트셀러 조회 성공")
    void getTop5BestSellerBooksSuccess() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findBestSellerBooks(any(Pageable.class))).thenReturn(page);
        PageResponse<MainBookResponse> result = bookService.getTop5BestSellerBooks();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("책 제목");
    }

    @Test
    @DisplayName("Top5 추천도서 조회 성공")
    void getTop5RecommendedBooksSuccess() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findRecommendedBooks(any(Pageable.class))).thenReturn(page);
        PageResponse<MainBookResponse> result = bookService.getTop5RecommendedBooks();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("책 제목");
    }

    @Test
    @DisplayName("Top5 신간 도서 조회 성공")
    void getTop5NewBooksSuccess() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAllByOrderByPublishedAtDesc(any(Pageable.class))).thenReturn(page);
        PageResponse<MainBookResponse> result = bookService.getTop5NewBooks();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("책 제목");
    }

    @Test
    @DisplayName("전체 베스트셀러 조회 성공")
    void getAllBestSellerBooksSuccess() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findBestSellerBooks(any(Pageable.class))).thenReturn(page);
        PageResponse<MainBookResponse> result = bookService.getAllBestSellerBooks(PageRequest.of(0, 5));
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("책 제목");
    }

    @Test
    @DisplayName("전체 신간 도서 조회 성공")
    void getAllNewBooksSuccess() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAllByOrderByPublishedAtDesc(any(Pageable.class))).thenReturn(page);
        PageResponse<MainBookResponse> result = bookService.getAllNewBooks(PageRequest.of(0, 5));
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("책 제목");
    }

    @Test
    @DisplayName("전체 추천 도서 조회 성공")
    void getAllRecommendedBooksSuccess() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findRecommendedBooks(any(Pageable.class))).thenReturn(page);
        PageResponse<MainBookResponse> result = bookService.getAllRecommendedBooks(PageRequest.of(0, 5));
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("책 제목");
    }
}
