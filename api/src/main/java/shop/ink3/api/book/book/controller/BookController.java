package shop.ink3.api.book.book.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.book.dto.BookCreateRequest;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.service.BookService;

@RestController
@RequestMapping("/api/books/books")
public class BookController {
    final
    BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;

    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.findAllByTitle(title));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.findAllByAuthor(author));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        return ResponseEntity.ok(bookService.findAllByAuthor(""));
    }

    @PostMapping
    public ResponseEntity<Book> addBooks(@RequestBody BookCreateRequest req) {
        return ResponseEntity.ok(bookService.save(req));
    }
}
