package shop.ink3.api.books.books.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.books.bookAuthors.entity.BookAuthors;
import shop.ink3.api.books.bookCategories.entity.BookCategories;
import shop.ink3.api.books.publishers.entity.Publishers;
import shop.ink3.api.cart.entity.Cart;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String ISBN;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publishers publishers;

    @Column(nullable = false)
    private LocalDate publishedAt;

    @Column(nullable = false)
    private Integer originalPrice;

    @Column(nullable = false)
    private Integer salePrice;

    @Column(nullable = false)
    private Integer discountRate;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookStatus status;

    @Column(nullable = false)
    private boolean isPackable;

    @Column(nullable = false)
    private String thumbnailUrl;

    @OneToMany(mappedBy = "books")
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "books",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BookCategories> bookCategories;

    @OneToMany(mappedBy = "books",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BookAuthors> bookAuthors;

    public void setDiscountRate() {
        this.discountRate = (salePrice / originalPrice) * 100;
    }
}
