package shop.ink3.api.book.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByIsbn(String isbn);

    // 태그이름기반 검색

    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN b.bookTags bt
        JOIN bt.tag t
        WHERE t.name IN :tagNames
    """)
    Page<Book> findDistinctByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);
}
