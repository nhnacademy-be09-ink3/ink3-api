package shop.ink3.api.books.categories.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank
        String name
) {}
