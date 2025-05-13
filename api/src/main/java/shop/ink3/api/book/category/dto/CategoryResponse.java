package shop.ink3.api.book.category.dto;

import shop.ink3.api.book.category.entity.Category;

public record CategoryResponse(
        Long id,
        String name,
        Long parentId
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCategory() != null ? category.getCategory().getId() : null
        );
    }
}