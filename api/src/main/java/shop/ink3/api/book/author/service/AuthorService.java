package shop.ink3.api.book.author.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.author.dto.AuthorCreateRequest;
import shop.ink3.api.book.author.dto.AuthorResponse;
import shop.ink3.api.book.author.dto.AuthorUpdateRequest;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.exception.AuthorNotFoundException;
import shop.ink3.api.book.author.repository.AuthorRepository;
import shop.ink3.api.common.dto.PageResponse;

@Transactional
@RequiredArgsConstructor
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public AuthorResponse getAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
        return AuthorResponse.from(author);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuthorResponse> getAuthors(Pageable pageable) {
        Page<Author> authors = authorRepository.findAll(pageable);
        return PageResponse.from(authors.map(AuthorResponse::from));
    }

    public AuthorResponse createAuthor(AuthorCreateRequest request) {
        Author author = Author.builder().name(request.name()).build();
        return AuthorResponse.from(authorRepository.save(author));
    }

    public AuthorResponse updateAuthor(Long authorId, AuthorUpdateRequest request) {
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
        author.update(request.name());
        return AuthorResponse.from(authorRepository.save(author));
    }

    public void deleteAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
        authorRepository.delete(author);
    }
}
