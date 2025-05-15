package shop.ink3.api.book.book.dto;

import java.util.List;
import shop.ink3.api.book.book.enums.BookSortType;

public record BookSearchRequest(
        String title,
        String author,
        Long categoryId,
        List<String> tagNames,
        Integer minPrice,        // 최소 가격
        Integer maxPrice,        // 최대 가격
        BookSortType sort,             // 정렬 조건
        Integer page,            // 페이지 번호 (0부터 시작)
        Integer size             // 페이지 크기 (기본 10)
) {
}