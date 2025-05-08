package shop.ink3.api.books.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
public class Categories {
    @Id
    private Long id;
    @NotNull
    @Length(max=20)
    String name;
    @OneToOne
    Categories categories;
}
