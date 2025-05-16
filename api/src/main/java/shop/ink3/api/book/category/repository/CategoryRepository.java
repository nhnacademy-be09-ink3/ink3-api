package shop.ink3.api.book.category.repository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.book.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(@NotNull @Length(max=20) String name);

    boolean existsByName(@NotNull @Length(max=20) String name);

    boolean existsByCategory(Category category);

}
