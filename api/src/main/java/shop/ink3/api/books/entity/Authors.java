package shop.ink3.api.books.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
public class Authors {
    @Id
    @NotNull
    private Long id;
    @Length(max=50)
    @NotNull
    String name;
    @NotNull
    Date date;
    @NotNull
    @Length(max=50)
    @NotNull
    String nationality;
    @NotNull
    String biography;
}
