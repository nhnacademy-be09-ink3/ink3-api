package shop.ink3.api.books.bookAuthors.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import shop.ink3.api.books.authors.entity.Authors;
import shop.ink3.api.books.books.entity.Books;

@Entity
@Getter
@Setter
public class BookAuthors {

    @Id
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne
    Books books;
    @JoinColumn(nullable = false)
    @ManyToOne
    Authors authors;
}
