package shop.ink3.api.book.book.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    /**
     * Retrieves detailed information for a specific book by its ID.
     *
     * @param bookId the unique identifier of the book to retrieve
     * @return a response containing the book details wrapped in a success response
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBook(bookId)));
    }

     /**
     * Retrieves a paginated list of all books.
     *
     * @param pageable pagination and sorting information
     * @return a response containing a paginated list of books
     */
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<BookResponse>>> getBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getBooks(pageable)));
    }

    /**
     * Retrieves the top 5 bestseller books.
     *
     * @return a response containing a list of the top 5 bestseller books wrapped in a common response structure
     */
    @GetMapping("/bestseller")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getTop5BestsellerBooks() {
        return ResponseEntity.ok(CommonResponse.success(bookService.getTop5BestSellerBooks()));
    }

    /**
     * Retrieves a paginated list of all bestseller books.
     *
     * @param pageable pagination information for the result set
     * @return a response containing a paginated list of bestseller books
     */
    @GetMapping("/bestseller-all")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getAllBestsellerBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getAllBestSellerBooks(pageable)));
    }

    /**
     * Retrieves the top 5 newest books.
     *
     * @return a response containing a list of the 5 most recently added books
     */
    @GetMapping("/new")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getTop5NewBooks() {
        return ResponseEntity.ok(CommonResponse.success(bookService.getTop5NewBooks()));
    }

    /**
     * Retrieves a paginated list of all new books.
     *
     * @param pageable pagination information for the result set
     * @return a response containing a paginated list of new books
     */
    @GetMapping("/new-all")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getAllNewBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getAllNewBooks(pageable)));
    }

    /**
     * Retrieves the top 5 recommended books.
     *
     * @return a response containing a list of the top 5 recommended books wrapped in a common response structure
     */
    @GetMapping("/recommend")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getTop5RecommendedBooks() {
        return ResponseEntity.ok(CommonResponse.success(bookService.getTop5RecommendedBooks()));
    }

    /**
     * Retrieves a paginated list of all recommended books.
     *
     * @param pageable pagination information for the result set
     * @return a response containing a paginated list of recommended books
     */
    @GetMapping("/recommend-all")
    public ResponseEntity<CommonResponse<PageResponse<MainBookResponse>>> getAllRecommendedBooks(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(bookService.getAllRecommendedBooks(pageable)));
    }

    /**
     * Creates a new book with the provided details.
     *
     * @param request the validated request body containing book creation information
     * @return a response entity with the created book wrapped in a success response and HTTP status 201
     */
    @PostMapping
    public ResponseEntity<CommonResponse<BookResponse>> createBook(@RequestBody @Valid BookCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(bookService.createBook(request)));
    }

    /****
     * Updates the details of an existing book.
     *
     * @param bookId the ID of the book to update
     * @param request the validated request body containing updated book information
     * @return a response entity containing the updated book details wrapped in a success response
     */
    @PutMapping("/{bookId}")
    public ResponseEntity<CommonResponse<BookResponse>> updateBook(@PathVariable Long bookId,
                                                                   @RequestBody @Valid BookUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(bookService.updateBook(bookId, request)));
    }

    /****
     * Deletes a book by its ID.
     *
     * Removes the specified book from the system and returns an HTTP 204 No Content response upon successful deletion.
     *
     * @param bookId the ID of the book to delete
     * @return HTTP 204 No Content if the deletion is successful
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
