package shop.ink3.api.book.book.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorDto(
        @NotBlank String name,
        @NotBlank String role
) {}
