package shop.ink3.api.book.author.repository;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import shop.ink3.api.book.author.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> getAuthorsByName(@Length(max=50) @NotNull String name);

    Optional<List<Author>> getAllByName(@Length(max=50) @NotNull String name);
}