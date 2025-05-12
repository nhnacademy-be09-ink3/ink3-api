package shop.ink3.api.book.author.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.author.repository.AuthorRepository;

@Service
@Transactional
public class AuthorService {
    AuthorRepository authorRepository;
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public void saveAuthors(Author author) {
        authorRepository.save(author);
    }

    public void deleteAuthors(Author author) {
        authorRepository.delete(author);
    }

    public void deleteAuthorsById(Long id) {
        authorRepository.deleteById(id);
    }

    public Optional<Author> findAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Optional<List<Author>> findAuthorByName(String name) {
        return authorRepository.getAllByName(name);
    }
}
