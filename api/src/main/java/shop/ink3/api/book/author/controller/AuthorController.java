package shop.ink3.api.book.author.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.book.author.dto.AuthorCreateRequest;
import shop.ink3.api.book.author.dto.AuthorResponse;
import shop.ink3.api.book.author.dto.AuthorUpdateRequest;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.service.AuthorService;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{authorId}")
    public ResponseEntity<CommonResponse<AuthorResponse>> getAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(CommonResponse.success(authorService.getAuthor(authorId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<AuthorResponse>>> getAuthors(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success(authorService.getAuthors(pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<AuthorResponse>> createAuthor(@RequestBody AuthorCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(authorService.createAuthor(request)));
    }

    @PutMapping("/{authorId}")
    public ResponseEntity<CommonResponse<AuthorResponse>> updateAuthor(@PathVariable Long authorId,
                                                                       @RequestBody AuthorUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(authorService.updateAuthor(authorId, request)));
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteAuthor(authorId);
        return ResponseEntity.noContent().build();
    }
}
