package shop.ink3.api.books.books.entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.ink3.api.books.bookAuthors.entity.BookAuthors;
import shop.ink3.api.books.bookCategories.entity.BookCategories;
import shop.ink3.api.books.entity.Publishers;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Books {
    @Id
    private Long id;
    @Column(nullable = false, length = 20)
    String ISBN;
    @Column(nullable = false)
    String title;
    @Column(nullable = false)
    String content;
    @Column(nullable = false)
    String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publisher_id", nullable = false)
    Publishers publishers;
    @Column(nullable = false)
    LocalDateTime publishedDate;
    @Column(nullable = false)
    int originalPrice;
    @Column(nullable = false)
    int salePrice;
    @Column(nullable = false)
    int discountRate;
    @Column(nullable = false)
    int quantity;
    @Column(nullable = false)
    Status status;
    @Column(nullable = false)
    boolean isPackable;
    @Column(nullable = false)
    String thumbnailUrl;
    @OneToMany(mappedBy = "books",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<BookCategories> bookCategories;
    @OneToMany(mappedBy = "books",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<BookAuthors> bookAuthors;

    public void setDiscountRate() {
        this.discountRate = (salePrice / originalPrice) * 100;
    }
}
