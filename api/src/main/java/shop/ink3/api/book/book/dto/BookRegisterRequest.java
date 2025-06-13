package shop.ink3.api.book.book.dto;

import java.util.List;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;

public record BookRegisterRequest(
        AladinBookResponse aladinBookResponse,
        Integer priceSales,
        Integer quantity,
        BookStatus status,
        Boolean isPackable,
        List<String> tags
) {
}
