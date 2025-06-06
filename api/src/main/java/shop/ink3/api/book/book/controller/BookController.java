package shop.ink3.api.book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.dto.MainBookResponse;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequestMapping("/books")
@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;
    private final ObjectMapper objectMapper;


    // 도서 상세 조회 (ID 기반 단건 조회)
    @GetMapping("/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBook(bookId)));
    }


    @GetMapping("/{bookId}/parent-categories")
    public ResponseEntity<CommonResponse<BookResponse>> getBookByIdWithParentCategory(@PathVariable Long bookId) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBookWithCategory(bookId)));
    }


    // 전체 도서 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<BookResponse>>> getBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBooks(pageable)));
    }

    @GetMapping("/bestseller")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getTop5BestsellerBooks() {
        return ResponseEntity.ok(CommonResponse.success(bookService.getTop5BestSellerBooks()));
    }

    @GetMapping("/bestseller-all")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getAllBestsellerBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getAllBestSellerBooks(pageable)));
    }

    @GetMapping("/new")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getTop5NewBooks() {
        return ResponseEntity.ok(CommonResponse.success(bookService.getTop5NewBooks()));
    }

    @GetMapping("/new-all")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getAllNewBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getAllNewBooks(pageable)));
    }

    @GetMapping("/recommend")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getTop5RecommendedBooks() {
        return ResponseEntity.ok(CommonResponse.success(bookService.getTop5RecommendedBooks()));
    }

    @GetMapping("/recommend-all")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getAllRecommendedBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getAllRecommendedBooks(pageable)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<BookResponse>> createBook(
            @RequestPart("book") String bookCreateRequestJson,
            @RequestPart("coverImage") MultipartFile coverImage
    ) {
        try {
            BookCreateRequest bookCreateRequest = objectMapper.readValue(bookCreateRequestJson, BookCreateRequest.class);
            BookResponse response = bookService.createBook(bookCreateRequest, coverImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> updateBook(@PathVariable Long bookId,
                                                                   @RequestBody @Valid BookUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(bookService.updateBook(bookId, request)));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
