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
        String publisherName,
        LocalDate publishedAt,
        Integer originalPrice,
        Integer salePrice,
        Integer discountRate,
        Integer quantity,
        BookStatus status,
        boolean isPackable,
        String thumbnailUrl,
        List<String> categoryNames,
        List<String> authorNames,
        List<String> tagNames
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getISBN(),
                book.getTitle(),
                book.getContents(),
                book.getDescription(),
                book.getPublisher().getName(),
                book.getPublishedAt(),
                book.getOriginalPrice(),
                book.getSalePrice(),
                book.getDiscountRate(),
                book.getQuantity(),
                book.getStatus(),
                book.isPackable(),
                book.getThumbnailUrl(),
                book.getBookCategories()
                        .stream()
                        .map(bc -> bc.getCategory().getName())
                        .toList(),
                book.getBookAuthors()
                        .stream()
                        .map(ba -> ba.getAuthor().getName())
                        .toList(),
                book.getBookTags()
                        .stream()
                        .map(bt -> bt.getTag().getName())
                        .toList()
        );
    }
}
