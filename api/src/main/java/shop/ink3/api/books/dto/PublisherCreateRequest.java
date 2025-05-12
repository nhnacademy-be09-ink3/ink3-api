package shop.ink3.api.books.dto;

import jakarta.validation.constraints.NotBlank;

public record PublisherCreateRequest(
        @NotBlank
        String name
) {}
