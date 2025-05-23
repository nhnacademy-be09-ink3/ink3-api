package shop.ink3.api.book.tag.dto;

import jakarta.validation.constraints.NotBlank;

public record TagUpdateRequest(
        @NotBlank
        String name
) {}