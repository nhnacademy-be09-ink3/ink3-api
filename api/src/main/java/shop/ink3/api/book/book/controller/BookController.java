package shop.ink3.api.book.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import shop.ink3.api.book.book.dto.BookRegisterRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.external.aladin.AladinClient;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final AladinClient aladinClient;

    // 도서 상세 조회 (ID 기반 단건 조회)
    @GetMapping("/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBook(bookId)));
    }

     // 전체 도서 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<BookResponse>>> getBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBooks(pageable)));
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

    // Keyword로 알라딘 API의 도서 리스트 조회, /aladin?keyword=도서
    @GetMapping("/aladin")
    public ResponseEntity<CommonResponse<PageResponse<AladinBookResponse>>> getBooksByKeyword(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(aladinClient.fetchBookByKeyword(keyword, pageable)));
    }

    // 알라딘 API에서 Keyword로 조회한 도서 리스트에서 하나의 도서를 선택하고 자체적으로 설정할 내용 입력하여 도서 등록
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<BookResponse>> registerBook(@RequestBody BookRegisterRequest request) {
        return ResponseEntity.ok(CommonResponse.success(bookService.registerBook(request)));
    }
}