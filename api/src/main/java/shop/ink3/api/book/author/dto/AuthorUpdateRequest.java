package shop.ink3.api.book.author.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record AuthorUpdateRequest (
        @NotBlank String name,
        @NotBlank LocalDate birth,
        @NotBlank String nationality,
        @NotBlank String biography
) {}
