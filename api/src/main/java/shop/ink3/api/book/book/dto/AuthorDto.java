package shop.ink3.api.book.book.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorDto(
        @NotBlank Long authorId,
        @NotBlank String authorName,
        @NotBlank String role
) {}
