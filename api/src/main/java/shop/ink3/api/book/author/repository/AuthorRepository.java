package shop.ink3.api.book.author.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.book.author.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}