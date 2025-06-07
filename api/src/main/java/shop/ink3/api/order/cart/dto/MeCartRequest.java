package shop.ink3.api.order.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MeCartRequest(
    @NotNull
    Long bookId,

    @Min(1)
    int quantity
) {
}
