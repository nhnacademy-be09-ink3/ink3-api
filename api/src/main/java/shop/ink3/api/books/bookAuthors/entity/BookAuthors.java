package shop.ink3.api.books.bookAuthors.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.books.authors.entity.Authors;
import shop.ink3.api.books.books.entity.Books;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class BookAuthors {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Books books;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Authors authors;
}
