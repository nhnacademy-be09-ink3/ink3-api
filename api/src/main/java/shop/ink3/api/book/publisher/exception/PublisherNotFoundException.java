package shop.ink3.api.book.publisher.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class PublisherNotFoundException extends NotFoundException {
    public PublisherNotFoundException(long publisherId) {
        super("Publisher not found. ID: %d".formatted(publisherId));
    }

    public PublisherNotFoundException(String publisherName) {
        super("Publisher not found. NAME: %s".formatted(publisherName));
    }
}
