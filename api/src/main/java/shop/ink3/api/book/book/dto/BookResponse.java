package shop.ink3.api.book.book.dto;

import java.time.LocalDate;
import java.util.List;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.category.dto.CategoryResponse;
import shop.ink3.api.book.tag.dto.TagResponse;

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
    List<TagResponse> tags,
    double averageRating
) {
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
            book.getBookCategories()
                    .stream()
                    .map(bc -> CategoryResponse.from(bc.getCategory()))
                    .toList(),
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
                .toList(),
            0.0
        );
    }

    public static BookResponse from(Book book, List<CategoryResponse> categories) {
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
                categories,
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
                        .toList(),
                0.0
        );
    }

    public static BookResponse from(Book book, double averageRating) {
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
            book.getBookCategories()
                    .stream()
                    .map(bc -> CategoryResponse.from(bc.getCategory()))
                    .toList(),
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
                .toList(),
            averageRating
        );
    }
}