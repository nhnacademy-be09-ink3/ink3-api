package shop.ink3.api.book.author.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.book.bookAuthor.entity.BookAuthor;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "author",
            orphanRemoval = true)
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    public void addBookAuthor(BookAuthor bookAuthor) {
        this.bookAuthors.add(bookAuthor);
    }

    public void update(String name) {
        this.name = name;
    }
}
