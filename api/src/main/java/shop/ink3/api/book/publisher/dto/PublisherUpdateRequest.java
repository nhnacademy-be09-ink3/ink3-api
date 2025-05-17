package shop.ink3.api.book.publisher.dto;

import jakarta.validation.constraints.NotBlank;

public record PublisherUpdateRequest(
        @NotBlank
        String name
) {}
