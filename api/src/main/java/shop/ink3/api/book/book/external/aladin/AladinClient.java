package shop.ink3.api.book.book.external.aladin;

import shop.ink3.api.book.book.external.aladin.dto.AladinBookDto;

public interface AladinClient {
    AladinBookDto fetchBookByIsbn(String isbn13);
}
