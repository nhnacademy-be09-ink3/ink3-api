package shop.ink3.api.books.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
public class Tags {
    @Id
    private Long id;
    @NotNull
    @Length(max=20)
    String name;
}
