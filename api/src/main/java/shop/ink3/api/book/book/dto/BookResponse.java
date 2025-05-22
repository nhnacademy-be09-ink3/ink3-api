package shop.ink3.api.book.book.dto;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;

import java.time.LocalDate;
import java.util.List;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;

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
        List<String> categoryNames,
        List<String> authorNames,
        List<String> tagName
) {
    public static BookResponse from(Book book) {
        int originalPrice = book.getOriginalPrice() != null ? book.getOriginalPrice() : 0;
        int salePrice = book.getSalePrice() != null ? book.getSalePrice() : 0;
        // 할인율 계산
        int discountRate = (originalPrice > 0)
                ? (int) Math.round((1 - (salePrice / (double) originalPrice)) * 100)
                : 0;

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
                discountRate,
                book.getQuantity() != null ? book.getQuantity() : 0,
                book.getStatus(),
                book.isPackable(),
                book.getThumbnailUrl(),
                book.getBookCategories()
                        .stream()
                        .map(bc -> bc.getCategory().getName())
                        .toList(),
                book.getBookAuthors()
                        .stream()
                        .map(ba -> ba.getAuthor().getName() + " (" + ba.getRole() + ")")
                        .toList(),
                book.getBookTags()
                        .stream()
                        .map(bt -> bt.getTag().getName())
                        .toList()
        );
    }
}