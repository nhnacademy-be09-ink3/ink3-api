package shop.ink3.api.book.author.dto;

import java.time.LocalDate;
import shop.ink3.api.book.author.entity.Author;

public record AuthorResponse (
    Long id,
    String name,
    LocalDate birth,
    String nationality,
    String biography
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBirth(),
                author.getNationality(),
                author.getBiography()
        );
    }
}
