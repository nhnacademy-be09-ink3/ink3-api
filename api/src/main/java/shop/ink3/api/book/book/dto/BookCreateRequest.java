package shop.ink3.api.book.book.dto;

import shop.ink3.api.book.book.entity.BookStatus;
import java.time.LocalDate;
import java.util.List;

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
        List<Long> categoryIds,
        List<Long> authorIds,
        List<Long> tagIds
) {}
