package shop.ink3.api.book.author.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
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

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false, length = 20)
    private String nationality;

    @Column(nullable = false)
    private String biography;

    @Builder.Default
    @OneToMany(mappedBy = "author",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    public void addBookAuthor(BookAuthor bookAuthor) {
        this.bookAuthors.add(bookAuthor);
    }

    public void update(String name, LocalDate birth, String nationality, String biography) {
        this.name = name;
        this.birth = birth;
        this.nationality = nationality;
        this.biography = biography;
    }
}
