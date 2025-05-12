package shop.ink3.api.books.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import shop.ink3.api.books.books.entity.Books;
import shop.ink3.api.books.categories.entity.Categories;

@Entity
@Getter
@Setter
public class BookCategories {
    @Id
    private Long id;

    @NotNull
    @ManyToOne
    Books books;

    @NotNull
    @ManyToOne
    Categories categories;
}
