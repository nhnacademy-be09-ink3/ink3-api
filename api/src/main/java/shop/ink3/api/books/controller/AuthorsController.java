package shop.ink3.api.books.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.books.entity.Authors;
import shop.ink3.api.books.service.AuthorsService;

@RestController
@RequestMapping("/api/books/authors")
public class AuthorsController {
    final
    AuthorsService authorsService;

    public AuthorsController(AuthorsService authorsService) {
        this.authorsService = authorsService;
    }

    @GetMapping("{authorName}")
    public ResponseEntity<List<Authors>> getAuthors(@PathVariable String authorName) {
        List<Authors> authorsList = authorsService.findAuthorByName(authorName).orElse(null);
        return ResponseEntity.ok(authorsList);
    }

    @PostMapping
    public ResponseEntity<Authors> saveAuthors(@RequestBody Authors authors) {
        authorsService.saveAuthors(authors);
        return ResponseEntity.ok(authors);
    }
}
