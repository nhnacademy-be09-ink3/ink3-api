package shop.ink3.api.books.repository;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.books.categories.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    Categories findByName(@NotNull @Length(max=20) String name);

    boolean existsByName(@NotNull @Length(max=20) String name);
}
