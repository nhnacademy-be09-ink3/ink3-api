package shop.ink3.api.order.cart.dto;

import jakarta.validation.constraints.Min;

public record CartUpdateRequest(
    @Min(1)
    int quantity
) {
}
