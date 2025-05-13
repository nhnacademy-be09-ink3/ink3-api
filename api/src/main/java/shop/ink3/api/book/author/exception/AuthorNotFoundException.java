package shop.ink3.api.book.author.exception;

public class AuthorNotFoundException extends RuntimeException {
  public AuthorNotFoundException(Long authorId) {
    super("Author not found. ID: %d".formatted(authorId));
  }
}
