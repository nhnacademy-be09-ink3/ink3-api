package shop.ink3.api.books.bookCategories.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import shop.ink3.api.books.books.entity.Books;
import shop.ink3.api.books.categories.entity.Categories;

@Entity
@Getter
public class BookCategories {
    @Id
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    Books books;

    @JoinColumn(nullable = false)
    @ManyToOne
    Categories category;
}
