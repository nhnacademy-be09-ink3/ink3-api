package shop.ink3.api.book.book.dto;

import java.time.LocalDate;
import java.util.List;

import shop.ink3.api.book.book.entity.BookStatus;

public record BookCreateRequest(
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
}
