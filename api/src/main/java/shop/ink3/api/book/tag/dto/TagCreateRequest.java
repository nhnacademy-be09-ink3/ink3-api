package shop.ink3.api.book.tag.dto;

import jakarta.validation.constraints.NotBlank;

public record TagCreateRequest(
        @NotBlank
        String name
) {}
