package shop.ink3.api.book.category.repository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.book.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(@NotNull @Length(max=20) String name);

    boolean existsByName(@NotNull @Length(max=20) String name);

    boolean existsByParent(Category category);

    @Query(value = """
        WITH RECURSIVE category_path AS (
            SELECT id, name, parent_id, 0 AS depth
            FROM categories
            WHERE id = :categoryId

            UNION ALL

            SELECT c.id, c.name, c.parent_id, cp.depth + 1 AS depth
            FROM categories c
            JOIN category_path cp ON c.id = cp.parent_id
        )
        SELECT *
        FROM category_path
        ORDER BY depth DESC
        """, nativeQuery = true)
    List<Category> findAllAncestors(@Param("categoryId") Long categoryId);
}
