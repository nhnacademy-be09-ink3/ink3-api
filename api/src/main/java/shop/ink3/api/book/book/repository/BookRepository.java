package shop.ink3.api.book.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.book.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
