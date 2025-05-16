package shop.ink3.api.book.book.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import shop.ink3.api.book.book.exception.InvalidBookSortTypeException;

public enum BookSortType {
    TITLE_ASC("title"),
    PUBLISHED_AT_DESC("publishedAt"),
    PRICE_ASC("priceAsc"),
    PRICE_DESC("priceDesc");

    private final String value;

    BookSortType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BookSortType from(String value) {
        for (BookSortType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidBookSortTypeException(value);
    }
}