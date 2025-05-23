package shop.ink3.api.book.author.dto;

import shop.ink3.api.book.author.entity.Author;

public record AuthorResponse (
    Long id,
    String name
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }
}
