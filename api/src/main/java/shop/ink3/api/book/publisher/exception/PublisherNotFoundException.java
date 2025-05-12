package shop.ink3.api.book.publisher.exception;

public class PublisherNotFoundException extends RuntimeException {
    public PublisherNotFoundException(long publisherId) {
        super("Publisher not found. ID: %d".formatted(publisherId));
    }

    public PublisherNotFoundException(String publisherName) {
        super("Publisher not found. NAME: %s".formatted(publisherName));
    }
}
