package shop.ink3.api.books.dto;

import shop.ink3.api.books.tags.entity.Tags;

public record TagResponse(
        Long id,
        String name
) {
    public static TagResponse from(Tags tags) {
        return new TagResponse(
                tags.getId(),
                tags.getName()
        );
    }
}