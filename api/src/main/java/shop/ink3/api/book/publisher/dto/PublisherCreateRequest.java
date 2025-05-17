package shop.ink3.api.book.publisher.dto;

import jakarta.validation.constraints.NotBlank;

public record PublisherCreateRequest(
        @NotBlank
        String name
) {}
