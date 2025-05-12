package shop.ink3.api.books.authors.repository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.books.authors.entity.Authors;


public interface AuthorsRepository extends JpaRepository<Authors, Long> {

    Optional<Authors> getAuthorsByName(@Length(max=50) @NotNull String name);

    Optional<List<Authors>> getAllByName(@Length(max=50) @NotNull String name);
}
