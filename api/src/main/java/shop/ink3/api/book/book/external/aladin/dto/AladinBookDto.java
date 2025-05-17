package shop.ink3.api.book.book.external.aladin.dto;

public record AladinBookDto(
        String title,
        String description,
        String toc,
        String author,
        String publisher,
        String pubDate,
        String isbn13,
        int priceStandard,
        int priceSales,
        String cover,
        String categoryName
) {}
