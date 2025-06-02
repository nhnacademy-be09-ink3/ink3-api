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
    List<String> authorNames
) {
    /**
     * Creates a MainBookResponse instance from a Book entity.
     *
     * Converts the Book's details into a DTO, formatting author names with their roles and defaulting null prices to zero.
     *
     * @param book the Book entity to convert
     * @return a MainBookResponse representing the book's main details
     */
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
            book.getBookAuthors()
                .stream()
                .map(ba -> ba.getAuthor().getName() + " (" + ba.getRole() + ")")
                .toList()
        );
    }
}
