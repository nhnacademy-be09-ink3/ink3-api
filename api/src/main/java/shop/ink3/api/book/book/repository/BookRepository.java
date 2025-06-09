package shop.ink3.api.book.book.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.category.entity.Category;

public interface BookRepository extends JpaRepository<Book, Long>, BookQuerydslRepository {

    boolean existsByIsbn(String isbn);

    @Query("""
        SELECT b
        FROM Book b
        JOIN OrderBook ob ON ob.book = b
        JOIN Order o ON ob.order = o
        WHERE o.status = 'DELIVERED'
        GROUP BY b.id
        ORDER BY SUM(ob.quantity) DESC
    """)
    Page<Book> findBestSellerBooks(Pageable pageable);

    Page<Book> findAllByOrderByPublishedAtDesc(Pageable pageable);

    @Query("""
        SELECT b
        FROM Book b
        JOIN Like l ON l.book = b
        GROUP BY b.id
        ORDER BY COUNT(l.id) DESC
    """)
    Page<Book> findRecommendedBooks(Pageable pageable);

    @Query("""
    SELECT DISTINCT b
    FROM Book b
    JOIN b.bookCategories bc
    JOIN bc.category c
    WHERE c.id IN :categoryIds
""")
    Page<Book> findByCategoryIds(List<Long> categoryIds, Pageable pageable);

    @Query("""
    SELECT c
    FROM Category c
    WHERE c.id = :categoryId OR c.parent.id = :categoryId
""")
    List<Category> findAllDescendantsIncludingSelf(@Param("categoryId") Long categoryId);



}
