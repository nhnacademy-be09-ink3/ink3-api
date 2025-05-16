package shop.ink3.api.book.book.dto;

import java.time.LocalDate;
import java.util.List;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;

public record BookResponse(
    Long id,
    String ISBN,
    String title,
    String contents,
    String description,
    LocalDate publishedAt,
    Integer originalPrice,
    Integer salePrice,
    Integer quantity,
    BookStatus status,
    boolean isPackable,
    String thumbnailUrl,
    Long publisherId,
    List<Long> categoryIdList,
    List<Long> authorIdList,
    List<Long> tagIdList
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
            book.getId(),
            book.getISBN(),
            book.getTitle(),
            book.getContents(),
            book.getDescription(),
            book.getPublishedAt(),
            book.getOriginalPrice(),
            book.getSalePrice(),
            book.getQuantity(),
            book.getStatus(),
            book.isPackable(),
            book.getThumbnailUrl(),
            book.getPublisher().getId(),
            book.getBookCategories().stream()
                .map(category -> category.getCategory().getId())
                .toList(),
            book.getBookAuthors().stream()
                .map(author -> author.getAuthor().getId())
                .toList(),
            book.getBookTags().stream()
                .map(tag -> tag.getTag().getId())
                .toList()
        );
    }
}
