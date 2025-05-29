package shop.ink3.api.book.book.external.aladin.client;

import org.springframework.data.domain.Pageable;
import shop.ink3.api.book.book.external.aladin.dto.AladinBookResponse;
import shop.ink3.api.common.dto.PageResponse;

public interface AladinClient {
    AladinBookResponse fetchBookByIsbn(String isbn13);
    PageResponse<AladinBookResponse> fetchBookByKeyword(String keyword, Pageable pageable);
}
