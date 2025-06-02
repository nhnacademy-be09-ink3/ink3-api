package shop.ink3.api.book.book.entity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.bookAuthor.entity.BookAuthor;
import shop.ink3.api.book.bookCategory.entity.BookCategory;
import shop.ink3.api.book.bookTag.entity.BookTag;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.book.tag.entity.Tag;

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
    private String isbn;

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

    @Builder.Default
    @OneToMany(mappedBy = "book",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<BookTag> bookTags = new ArrayList<>();

    /**
     * Calculates and updates the discount rate based on the original and sale prices before persisting or updating the entity.
     *
     * If the original price is zero, sets the discount rate to zero to avoid division by zero.
     */
    @PrePersist
    @PreUpdate
    public void updateDiscountRate() {
        if (originalPrice != 0) {
            this.discountRate = (originalPrice - salePrice) * 100 / originalPrice;
        } else {
            this.discountRate = 0;
        }
    }

    /**
     * Associates this book with the specified category by creating and linking a new BookCategory entity.
     *
     * @param category the category to associate with this book
     */
    public void addBookCategory(Category category) {
        BookCategory bookCategory = new BookCategory(this, category);
        this.bookCategories.add(bookCategory);
        category.addBookCategory(bookCategory);
    }

    public void addBookAuthor(Author author, String role) {
        BookAuthor bookAuthor = new BookAuthor(this, author, role);
        this.bookAuthors.add(bookAuthor);
        author.addBookAuthor(bookAuthor);
    }

    public void addBookTag(Tag tag) {
        BookTag bookTag = new BookTag(this, tag);
        this.bookTags.add(bookTag);
        tag.addBookTag(bookTag);
    }

    public void updateBook(
            String isbn,
            String title,
            String contents,
            String description,
            LocalDate publishedAt,
            Integer originalPrice,
            Integer salePrice,
            Integer quantity,
            BookStatus status,
            boolean isPackable,
            String thumbnailUrl,
            Publisher publisher
    ) {
        this.isbn = isbn;
        this.title = title;
        this.contents = contents;
        this.description = description;
        this.publishedAt = publishedAt;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.status = status;
        this.isPackable = isPackable;
        this.thumbnailUrl = thumbnailUrl;
        this.publisher = publisher;
    }

    // 주문 시 재고 확인 및 재고 수량 감소
    public void decreaseQuantity(int amount) {
        if (this.quantity < amount) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.quantity -= amount;
    }

    // 반품 시 재고 수량 증가
    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}
