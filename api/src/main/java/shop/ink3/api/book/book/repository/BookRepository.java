package shop.ink3.api.book.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 제목에 특정 문자열이 포함된 도서 목록
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 저자 이름 기반 검색
    Page<Book> findDistinctByBookAuthorsAuthorNameContainingIgnoreCase(String author, Pageable pageable);

    // 단순 제목 기반
    List<Book> getBooksByTitle(String title);

    List<Book> findDistinctByBookAuthorsAuthorNameContainingIgnoreCase(String name);

    boolean existsByISBN(String isbn);
}
