package shop.ink3.api.book.author.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorUpdateRequest (
        @NotBlank String name
) {}
