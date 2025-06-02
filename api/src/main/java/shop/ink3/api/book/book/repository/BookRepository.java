package shop.ink3.api.book.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.ink3.api.book.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    /****
 * Checks if a book with the specified ISBN exists in the repository.
 *
 * @param isbn the ISBN to check for existence
 * @return true if a book with the given ISBN exists, false otherwise
 */
boolean existsByIsbn(String isbn);

    /****
     * Retrieves a paginated list of best-selling books based on the total quantity sold in delivered orders.
     *
     * @param pageable pagination and sorting information
     * @return a page of books ordered by total quantity sold in descending order
     */
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

    /****
 * Retrieves a paginated list of all books ordered by publication date in descending order.
 *
 * @param pageable pagination and sorting information
 * @return a page of books sorted by most recently published first
 */
Page<Book> findAllByOrderByPublishedAtDesc(Pageable pageable);

    /****
     * Retrieves a paginated list of books ordered by the number of likes in descending order.
     *
     * @param pageable pagination information
     * @return a page of books with the most likes first
     */
    @Query("""
        SELECT b
        FROM Book b
        JOIN Like l ON l.book = b
        GROUP BY b.id
        ORDER BY COUNT(l.id) DESC
    """)
    Page<Book> findRecommendedBooks(Pageable pageable);

}
