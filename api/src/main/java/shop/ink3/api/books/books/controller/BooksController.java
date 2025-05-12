package shop.ink3.api.books.books.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.books.books.entity.Books;
import shop.ink3.api.books.books.service.BooksService;

@RestController
@RequestMapping("/api/books/books")
public class BooksController {
    final
    BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Books>> getBooksByTitle(@PathVariable String title) {
        return ResponseEntity.ok(booksService.findAllByTitle(title));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Books>> getBooksByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(booksService.findAllByAuthor(author));
    }

    @GetMapping
    public ResponseEntity<List<Books>> getBooks() {
        return ResponseEntity.ok(booksService.findAllByAuthor(""));
    }

    @PostMapping
    public ResponseEntity<Books> addBooks(@RequestBody Books books) {
        booksService.save(books);
        Books newBooks = new Books();
        newBooks.setTitle(books.getTitle());
        return ResponseEntity.ok(newBooks);
    }
}
