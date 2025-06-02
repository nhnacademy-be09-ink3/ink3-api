package shop.ink3.api.book.book.external.aladin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.book.dto.BookRegisterRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.external.aladin.client.AladinClient;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/aladin")
public class AladinController {

    private final BookService bookService;
    private final AladinClient aladinClient;

    /**
     * Retrieves a paginated list of books from the Aladin API that match the specified keyword.
     *
     * @param keyword the search keyword to filter books
     * @param pageable pagination information for the result set
     * @return a response entity containing a common response with a page of Aladin book data
     */
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<AladinBookResponse>>> getBooksByKeyword(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(aladinClient.fetchBookByKeyword(keyword, pageable)));
    }

    /**
     * Registers a new book in the system using details provided in the request.
     *
     * Accepts book information selected from the Aladin API along with additional user-defined details, and registers the book in the system.
     *
     * @param request the book registration data, including Aladin book selection and custom fields
     * @return a response containing the registered book's information
     */
    @PostMapping("/register-book")
    public ResponseEntity<CommonResponse<BookResponse>> registerBook(@RequestBody @Valid BookRegisterRequest request) {
        return ResponseEntity.ok(CommonResponse.success(bookService.registerBook(request)));
    }
}
