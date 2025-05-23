package shop.ink3.api.book.author.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorCreateRequest (
        @NotBlank String name
) {}