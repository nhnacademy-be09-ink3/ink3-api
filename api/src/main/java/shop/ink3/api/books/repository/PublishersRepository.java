package shop.ink3.api.books.repository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.books.entity.Publishers;

public interface PublishersRepository extends JpaRepository<Publishers, Long> {
    Optional<Publishers> findByName(@NotNull @Length(max=20) String name);
    boolean existsByName(@NotNull @Length(max=20) String name);
}
