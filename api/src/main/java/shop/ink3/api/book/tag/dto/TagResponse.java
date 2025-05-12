package shop.ink3.api.book.tag.dto;

import shop.ink3.api.book.tag.entity.Tag;

public record TagResponse(
        Long id,
        String name
) {
    public static TagResponse from(Tag tag) {
        return new TagResponse(
                tag.getId(),
                tag.getName()
        );
    }
}