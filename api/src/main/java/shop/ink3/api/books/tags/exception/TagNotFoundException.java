package shop.ink3.api.books.tags.exception;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(long tagId) {
        super("Tag not found. ID: %d".formatted(tagId));
    }

    public TagNotFoundException(String name) {
        super("Tag not found. Name: %s".formatted(name));
    }
}
