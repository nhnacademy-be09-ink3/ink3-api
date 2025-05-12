package shop.ink3.api.review.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO: 임시
    private Long userId;
    //TODO: 임시
    private Long orderBookId;

    private String title;
    private String content;
    private int rating;

    private LocalDateTime createdAt;

    public Review(Long userId, Long orderBookId, String title, String content, int rating) {
        this.userId = userId;
        this.orderBookId = orderBookId;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }
}
