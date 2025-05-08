package shop.ink3.api.books.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BookTags {
    @Id
    private Long id;
    @NotNull
    @ManyToOne
    Books books;
    @NotNull
    @ManyToOne
    Tags tags;
}
