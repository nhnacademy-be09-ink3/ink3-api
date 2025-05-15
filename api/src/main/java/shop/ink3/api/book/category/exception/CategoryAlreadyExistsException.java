package shop.ink3.api.book.category.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String name) {
        super("Category already exists. NAME: %s".formatted(name));
    }
}