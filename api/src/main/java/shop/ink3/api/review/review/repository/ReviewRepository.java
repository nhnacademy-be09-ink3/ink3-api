package shop.ink3.api.review.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.ink3.api.review.review.dto.ReviewListResponse;
import shop.ink3.api.review.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findReviewByUserId(Long userId);

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
