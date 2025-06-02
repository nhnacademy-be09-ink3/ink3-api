package shop.ink3.api.book.book.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import shop.ink3.api.book.author.entity.Author;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.bookCategory.entity.BookCategory;
import shop.ink3.api.book.bookTag.entity.BookTag;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.tag.dto.TagResponse;
import shop.ink3.api.book.tag.entity.Tag;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String contents,
        String description,
        String publisherName,
        LocalDate publishedAt,
        int originalPrice,
        int salePrice,
        int discountRate,
        int quantity,
        BookStatus status,
        boolean isPackable,
        String thumbnailUrl,
        List<CategoryResponse> categories,
        List<AuthorDto> authors,
        List<TagResponse> tags
) {
    /****
     * Creates a {@link BookResponse} from a {@link Book} entity, mapping all relevant fields and associated entities.
     *
     * Converts the book's categories, authors, and tags into their respective DTO representations. Handles null values for pricing, publisher, and quantity by providing default values.
     *
     * @param book the {@link Book} entity to convert
     * @return a fully populated {@link BookResponse} representing the given book
     */
    public static BookResponse from(Book book) {
        int originalPrice = book.getOriginalPrice() != null ? book.getOriginalPrice() : 0;
        int salePrice = book.getSalePrice() != null ? book.getSalePrice() : 0;

        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getContents(),
                book.getDescription(),
                book.getPublisher() != null ? book.getPublisher().getName() : null,
                book.getPublishedAt(),
                originalPrice,
                salePrice,
                book.getDiscountRate(),
                book.getQuantity() != null ? book.getQuantity() : 0,
                book.getStatus(),
                book.isPackable(),
                book.getThumbnailUrl(),
                book.getBookCategories().stream().map(bc -> CategoryResponse.from(bc.getCategory())).toList(),
                book.getBookAuthors()
                        .stream()
                        .map(ba -> new AuthorDto(
                                ba.getAuthor().getId(),
                                ba.getAuthor().getName(),  // Author 엔티티에 name 필드가 있다고 가정
                                ba.getRole()
                        ))
                        .toList(),
                book.getBookTags()
                        .stream()
                        .map(bt -> TagResponse.from(bt.getTag()))
                        .toList()
        );
    }

    /**
     * Builds a hierarchical path string for the given category by traversing from the category up to its root parent.
     *
     * The resulting path lists category names from root to leaf, separated by the '>' character.
     *
     * @param category the starting category
     * @return the category path as a string, or an empty string if the input is null
     */
    private static String buildCategoryPath(Category category) {
        List<String> categoryNames = new ArrayList<>();
        while (category != null) {
            categoryNames.add(category.getName());
            category = category.getParent();
        }
        Collections.reverse(categoryNames);
        return String.join(">", categoryNames);
    }

}