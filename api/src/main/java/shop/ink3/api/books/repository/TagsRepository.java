package shop.ink3.api.books.repository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.books.entity.Tags;

public interface TagsRepository extends JpaRepository<Tags, Long> {
    Optional<Tags> findByName(@NotNull @Length(max=20) String tagName);
    boolean existsByName(@NotNull @Length(max=20) String name);
}
