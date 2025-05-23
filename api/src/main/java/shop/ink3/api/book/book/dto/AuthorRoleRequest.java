package shop.ink3.api.book.book.dto;

public record AuthorRoleRequest(
        Long authorId,
        String role
) {}
