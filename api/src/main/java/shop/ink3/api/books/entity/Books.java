package shop.ink3.api.books.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Books {
    @Id
    private Long id;
    @NotNull
    @Length(max=20)
    String ISBN;
    @NotNull
    String title;
    @NotNull
    String content;
    @NotNull
    String description;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publisher_id")  // FK 컬럼명
    Publishers publishers;
    @NotNull
    LocalDateTime publishedDate;
    @NotNull
    int originalPrice;
    @NotNull
    int salePrice;
    @NotNull
    int discountRate;
    @NotNull
    int quantity;
    @NotNull
    Status status;
    @NotNull
    boolean isPackable;
    @NotNull
    String thumbnailUrl;

    public void setDiscountRate() {
        this.discountRate = (salePrice / originalPrice) * 100;
    }
}
