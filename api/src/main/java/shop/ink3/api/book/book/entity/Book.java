package shop.ink3.api.book.book.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.bookAuthor.entity.BookAuthor;
import shop.ink3.api.book.bookCategory.entity.BookCategory;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.publisher.entity.Publisher;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "books")
public class Book {
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
    private Publisher publisher;

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

    @Builder.Default
    @OneToMany(mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BookCategory> bookCategories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void setDiscountRate() {
        this.discountRate = (originalPrice - salePrice) * 100 / originalPrice;
    }

    public void addBookCategory(Category category) {
        BookCategory bookCategory = new BookCategory(this, category);
        this.bookCategories.add(bookCategory);
        category.addBookCategory(bookCategory);
    }

    public void addBookAuthor(Author author) {
        BookAuthor bookAuthor = new BookAuthor(this, author);
        this.bookAuthors.add(bookAuthor);
        author.addBookAuthor(bookAuthor);
    }
}
