package shop.ink3.api.book.author.exception;

public class AuthorNotFoundException extends RuntimeException{
    public AuthorNotFoundException(long authorId) {
        super("Author not found. ID: %d".formatted(authorId));
    }

    public AuthorNotFoundException(String authorName) {
        super("Author not found. NAME: %s".formatted(authorName));
    }
}