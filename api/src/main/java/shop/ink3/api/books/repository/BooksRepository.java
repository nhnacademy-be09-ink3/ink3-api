package shop.ink3.api.books.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.books.books.entity.Books;

public interface BooksRepository extends JpaRepository<Books, Long> {
    List<Books> getBooksByTitle(String title);

    List<Books> findDistinctByBookAuthorsAuthorsNameContainingIgnoreCase(String name);
}
