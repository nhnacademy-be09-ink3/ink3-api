package shop.ink3.api.book.category.exception;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(long categoryId) {
        super("Category not found. ID: %d".formatted(categoryId));
    }

    public CategoryNotFoundException(String categoryName) {
        super("Category not found. NAME: %s".formatted(categoryName));
    }
}