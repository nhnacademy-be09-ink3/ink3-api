package shop.ink3.api.book.book.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.dto.BookResponse;
import shop.ink3.api.book.book.dto.BookUpdateRequest;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.service.BookService;
import shop.ink3.api.common.dto.CommonResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/books")
public class BookController {

    private final BookService bookService;

//    @GetMapping("/title/{title}")
//    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
//        return ResponseEntity.ok(bookService.findAllByTitle(title));
//    }
//
//    @GetMapping("/author/{author}")
//    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
//        return ResponseEntity.ok(bookService.findAllByAuthor(author));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Book>> getBooks() {
//        return ResponseEntity.ok(bookService.findAllByAuthor(""));
//    }

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
}
