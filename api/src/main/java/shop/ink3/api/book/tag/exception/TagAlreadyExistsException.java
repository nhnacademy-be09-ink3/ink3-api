package shop.ink3.api.book.tag.exception;

public class TagAlreadyExistsException extends RuntimeException {
    public TagAlreadyExistsException(String name) {
        super("Tag already exists. NAME: %s".formatted(name));
    }
}
