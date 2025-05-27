package shop.ink3.api.review.review.entity;

import java.time.LocalDateTime;

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
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.user.user.entity.User;

@Builder
@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review {
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

    private LocalDateTime createdAt;
    // private LocalDateTime modifiedAt;

    public Review(User user, OrderBook orderBook, String title, String content, int rating) {
        this.user = user;
        this.orderBook = orderBook;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content, int rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        // this.modifiedAt = LocalDateTime.now();
    }
}
