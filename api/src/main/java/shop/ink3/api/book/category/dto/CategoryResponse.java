package shop.ink3.api.book.category.dto;

import shop.ink3.api.book.category.entity.Category;

import java.util.ArrayList;
import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        Long parentId,
        List<CategoryResponse> children
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                new ArrayList<>()
        );
    }
}