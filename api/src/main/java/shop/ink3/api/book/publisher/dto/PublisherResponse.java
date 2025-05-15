package shop.ink3.api.book.publisher.dto;


import shop.ink3.api.book.publisher.entity.Publisher;

public record PublisherResponse(
        Long id,
        String name
) {
    public static PublisherResponse from(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName()
        );
    }
}
