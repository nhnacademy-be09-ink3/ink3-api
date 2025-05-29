package shop.ink3.api.book.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;

    // 도서 상세 조회 (ID 기반 단건 조회)
    @GetMapping("/books/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBook(bookId)));
    }

     // 전체 도서 목록 조회
    @GetMapping("/books")
    public ResponseEntity<CommonResponse<PageResponse<BookResponse>>> getBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBooks(pageable)));
    }

    @PostMapping("/admin/books")
    public ResponseEntity<CommonResponse<BookResponse>> createBook(@RequestBody @Valid BookCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(bookService.createBook(request)));
    }

    @PutMapping("/admin/books/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> updateBook(@PathVariable Long bookId,
                                                                   @RequestBody @Valid BookUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(bookService.updateBook(bookId, request)));
    }

    @DeleteMapping("/admin/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}