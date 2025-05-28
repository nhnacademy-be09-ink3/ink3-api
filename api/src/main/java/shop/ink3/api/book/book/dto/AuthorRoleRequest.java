package shop.ink3.api.book.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthorRoleRequest(
        @NotNull Long authorId,
        @NotBlank String role
) {}
