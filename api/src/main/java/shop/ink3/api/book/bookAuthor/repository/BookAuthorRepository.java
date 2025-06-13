package shop.ink3.api.book.bookAuthor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import shop.ink3.api.book.bookAuthor.entity.BookAuthor;

public interface BookAuthorRepository extends CrudRepository<BookAuthor, Long> {
    @EntityGraph(attributePaths = "author")
    List<BookAuthor> findAllByBookId(Long bookId);

    void deleteAllByBookId(Long bookId);
}
