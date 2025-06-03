package shop.ink3.api.book.author.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class AuthorNotFoundException extends NotFoundException {
  public AuthorNotFoundException(Long authorId) {
    super("Author not found. ID: %d".formatted(authorId));
  }
  public AuthorNotFoundException(String message) {
    super("Author not found. name: %s".formatted(message));
  }
}