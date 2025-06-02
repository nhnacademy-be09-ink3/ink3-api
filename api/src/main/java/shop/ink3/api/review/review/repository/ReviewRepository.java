package shop.ink3.api.review.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.ink3.api.review.review.dto.ReviewListResponse;
import shop.ink3.api.review.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    /****
 * Retrieves the review written by the user with the specified user ID.
 *
 * @param userId the ID of the user whose review is to be retrieved
 * @return the Review entity associated with the given user ID, or null if none exists
 */
Review findReviewByUserId(Long userId);

    /**
     * Retrieves a paginated list of review summaries for a specific book.
     *
     * Each summary includes review and user details, order book ID, review content, rating, and timestamps.
     *
     * @param pageable pagination information
     * @param bookId the ID of the book for which to fetch reviews
     * @return a page of review summary DTOs for the specified book
     */
    @Query("""
            SELECT new shop.ink3.api.review.review.dto.ReviewListResponse(
                r.id,
                u.id,
                ob.id,
                u.name,
                r.title,
                r.content,
                r.rating,
                r.createdAt,
                r.modifiedAt
            )
            FROM Review r
            JOIN r.user u
            JOIN r.orderBook ob
            JOIN ob.book b
            WHERE b.id = :bookId
        """)
    Page<ReviewListResponse> findListByBookId(Pageable pageable, @Param("bookId") Long bookId);
}
