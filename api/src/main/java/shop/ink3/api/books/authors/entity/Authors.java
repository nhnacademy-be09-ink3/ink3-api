package shop.ink3.api.books.authors.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Authors {
    @Id
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false, length = 50)
    String name;
    @Column(nullable = false)
    Date date;
    @Column(nullable = false, length = 50)
    String nationality;
    @Column(nullable = false)
    String biography;
}
