package shop.ink3.api.review.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.common.entity.BaseTimeEntity;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.user.user.entity.User;

@Builder
@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "order_book_id")
    private OrderBook orderBook;

    @Column(length = 50)
    private String title;
    private String content;
    private int rating;

    /**
     * Constructs a new Review with the specified user, order book, title, content, and rating.
     *
     * @param user the user who wrote the review
     * @param orderBook the associated order book, or null if not applicable
     * @param title the title of the review
     * @param content the content of the review
     * @param rating the rating given in the review
     */
    public Review(User user, OrderBook orderBook, String title, String content, int rating) {
        this.user = user;
        this.orderBook = orderBook;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    /**
     * Updates the title, content, and rating of this review.
     *
     * @param title   the new title for the review
     * @param content the new content for the review
     * @param rating  the new rating value
     */
    public void update(String title, String content, int rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
    }
}
