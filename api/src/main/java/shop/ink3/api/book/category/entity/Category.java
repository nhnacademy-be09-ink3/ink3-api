package shop.ink3.api.book.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import shop.ink3.api.book.bookCategory.entity.BookCategory;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Length(max=20)
    private String name;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "category",
            orphanRemoval = true)
    private List<BookCategory> bookCategories = new ArrayList<>();

    public void addBookCategory(BookCategory bookCategory) {
        this.bookCategories.add(bookCategory);
    }

    public void updateCategoryName(@Length(max=20) String name) {
        this.name = name;
    }

    public void updateParentCategory(Category category) {
        this.category = category;
    }
}
