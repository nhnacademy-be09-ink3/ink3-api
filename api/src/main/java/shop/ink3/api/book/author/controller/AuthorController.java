package shop.ink3.api.book.author.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.service.AuthorService;

@RestController
@RequestMapping("/api/books/authors")
public class AuthorController {
    final
    AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("{authorName}")
    public ResponseEntity<List<Author>> getAuthors(@PathVariable String authorName) {
        List<Author> authorList = authorService.findAuthorByName(authorName).orElse(null);
        return ResponseEntity.ok(authorList);
    }

    @PostMapping
    public ResponseEntity<Author> saveAuthors(@RequestBody Author author) {
        authorService.saveAuthors(author);
        return ResponseEntity.ok(author);
    }
}
