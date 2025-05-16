package shop.ink3.api.book.publisher.exception;

import shop.ink3.api.common.exception.AlreadyExistsException;

public class PublisherAlreadyExistsException extends AlreadyExistsException {
    public PublisherAlreadyExistsException(String name) {
        super("Publisher already exists. NAME: %s".formatted(name));
    }
}
