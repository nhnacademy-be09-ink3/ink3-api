package shop.ink3.api.book.book.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookSearchRequest;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/books")
public class BookController {
    private final BookService bookService;

    // 도서 제목으로 도서 목록 조회 API

    @GetMapping("/title/{title}")
    public ResponseEntity<List<BookResponse>> getBooksByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.findAllByTitle(title));
    }

    // 저자 이름으로 도서 목록 조회 API

    @GetMapping("/author/{author}")
    public ResponseEntity<List<BookResponse>> getBooksByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.findAllByAuthor(author));
    }

     // 전체 도서 목록 조회 (현재는 저자 이름이 빈 문자열일 경우 전체 조회로 활용)

    @GetMapping
    public ResponseEntity<List<BookResponse>> getBooks() {
        return ResponseEntity.ok(bookService.findAllByAuthor(""));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<BookResponse>> createBook(@RequestBody BookCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(bookService.createBook(request)));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> updateBook(@PathVariable Long bookId,
                                                                   @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(bookService.updateBook(bookId, request)));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

     // ISBN을 통해 알라딘 API에서 도서정보 자동등록

    @PostMapping("/register-by-isbn")
    public ResponseEntity<BookResponse> registerByIsbn(@RequestParam String isbn) {
        return ResponseEntity.ok(bookService.registerBookByIsbn(isbn));
    }

     // 도서검색 API (제목 또는 저자 기반검색)

    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> searchBooks(
            @ModelAttribute BookSearchRequest request) {
        return ResponseEntity.ok(bookService.searchBooks(request));
    }

    // 도서상세조회 API (ID 기반 단건 조회)

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }
}