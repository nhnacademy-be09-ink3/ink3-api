package shop.ink3.api.books.publishers.dto;

import jakarta.validation.constraints.NotBlank;

public record PublisherUpdateRequest(
        @NotBlank
        String name
) {}
