package shop.ink3.api.book.book.dto;

import java.util.List;

import shop.ink3.api.book.book.entity.Book;

public record MainBookResponse(
    Long id,
    String title,
    int originalPrice,
    int salePrice,
    int discountRate,
    String thumbnailUrl,
    boolean isPackable,
    List<String> authorNames,
    long reviewCount,
    long likeCount
) {
    public static MainBookResponse from(Book book) {
        int originalPrice = book.getOriginalPrice() != null ? book.getOriginalPrice() : 0;
        int salePrice = book.getSalePrice() != null ? book.getSalePrice() : 0;

        return new MainBookResponse(
            book.getId(),
            book.getTitle(),
            originalPrice,
            salePrice,
            book.getDiscountRate(),
            book.getThumbnailUrl(),
            book.isPackable(),
            book.getBookAuthors()
                .stream()
                .map(ba -> ba.getAuthor().getName() + " (" + ba.getRole() + ")")
                .toList(),
            0L,
            0L
        );
    }

    public static MainBookResponse from(Book book, long reviewCount, long likeCount) {
        int originalPrice = book.getOriginalPrice() != null ? book.getOriginalPrice() : 0;
        int salePrice = book.getSalePrice() != null ? book.getSalePrice() : 0;

        return new MainBookResponse(
            book.getId(),
            book.getTitle(),
            originalPrice,
            salePrice,
            book.getDiscountRate(),
            book.getThumbnailUrl(),
            book.isPackable(),
            book.getBookAuthors()
                .stream()
                .map(ba -> ba.getAuthor().getName() + " (" + ba.getRole() + ")")
                .toList(),
            reviewCount,
            likeCount
        );
    }
}
