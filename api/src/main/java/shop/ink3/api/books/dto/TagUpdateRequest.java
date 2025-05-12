package shop.ink3.api.books.dto;

import jakarta.validation.constraints.NotBlank;

public record TagUpdateRequest(
        @NotBlank
        String name
) {}