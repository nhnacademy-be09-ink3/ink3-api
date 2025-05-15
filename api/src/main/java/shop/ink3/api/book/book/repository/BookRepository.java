package shop.ink3.api.book.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.book.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> getBooksByTitle(String title);

    List<Book> findDistinctByBookAuthorsAuthorNameContainingIgnoreCase(String name);
}
