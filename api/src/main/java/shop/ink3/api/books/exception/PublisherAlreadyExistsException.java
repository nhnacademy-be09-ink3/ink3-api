package shop.ink3.api.books.exception;

public class PublisherAlreadyExistsException extends RuntimeException {
    public PublisherAlreadyExistsException(String name) {
        super("Tag already exists. NAME: %s".formatted(name));
    }
}
