package shop.ink3.api.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.books.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
}
