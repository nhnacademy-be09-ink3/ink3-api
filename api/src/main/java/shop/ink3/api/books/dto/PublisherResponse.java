package shop.ink3.api.books.dto;


import shop.ink3.api.books.entity.Publishers;

public record PublisherResponse(
        Long id,
        String name
) {
    public static PublisherResponse from(Publishers publishers) {
        return new PublisherResponse(
                publishers.getId(),
                publishers.getName()
        );
    }
}
