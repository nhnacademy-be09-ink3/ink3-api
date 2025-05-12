package shop.ink3.api.book.publisher.exception;

public class PublisherAlreadyExistsException extends RuntimeException {
    public PublisherAlreadyExistsException(String name) {
        super("Tag already exists. NAME: %s".formatted(name));
    }
}
