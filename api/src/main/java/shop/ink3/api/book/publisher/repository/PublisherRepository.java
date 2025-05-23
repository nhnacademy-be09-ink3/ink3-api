package shop.ink3.api.book.publisher.repository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.book.publisher.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByName(@NotNull @Length(max=20) String name);
    boolean existsByName(@NotNull @Length(max=20) String name);
}
