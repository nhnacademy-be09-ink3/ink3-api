package shop.ink3.api.book.author.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class AuthorNotFoundException extends NotFoundException {
  /****
   * Exception indicating that an author with the specified ID was not found.
   *
   * @param authorId the unique identifier of the author that could not be found
   */
  public AuthorNotFoundException(Long authorId) {
    super("Author not found. ID: %d".formatted(authorId));
  }
  /**
   * Constructs an exception indicating that an author with the specified name was not found.
   *
   * @param message the name of the author that could not be found
   */
  public AuthorNotFoundException(String message) {
    super("Author not found. name: %s".formatted(message));
  }
}