package shop.ink3.api.books.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import shop.ink3.api.books.entity.Authors;
import shop.ink3.api.books.repository.AuthorsRepository;

@Service
public class AuthorsService {
    AuthorsRepository authorsRepository;
    public AuthorsService(AuthorsRepository authorsRepository) {
        this.authorsRepository = authorsRepository;
    }

    public void saveAuthors(Authors authors) {
        authorsRepository.save(authors);
    }

    public void deleteAuthors(Authors authors) {
        authorsRepository.delete(authors);
    }

    public void deleteAuthorsById(Long id) {
        authorsRepository.deleteById(id);
    }

    public Optional<Authors> findAuthorById(Long id) {
        return authorsRepository.findById(id);
    }

    public Optional<List<Authors>> findAuthorByName(String name) {
        return authorsRepository.getAllByName(name);
    }
}
