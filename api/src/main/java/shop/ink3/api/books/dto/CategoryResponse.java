package shop.ink3.api.books.dto;

import shop.ink3.api.books.categories.entity.Categories;
public record CategoryResponse(
        Long id,
        String name,
        Categories categories
) {
    public static CategoryResponse from(Categories categories) {
        return new CategoryResponse(
                categories.getId(),
                categories.getName(),
                categories.getCategories()
        );
    }
}