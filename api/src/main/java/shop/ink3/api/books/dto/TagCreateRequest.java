package shop.ink3.api.books.dto;

import jakarta.validation.constraints.NotBlank;

public record TagCreateRequest(
        @NotBlank
        String name
) {}
