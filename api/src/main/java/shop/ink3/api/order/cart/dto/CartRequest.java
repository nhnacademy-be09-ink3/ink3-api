package shop.ink3.api.order.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartRequest(
    @NotNull(message = "사용자 ID는 필수입니다.")
    Long userId,

    @NotNull(message = "도서 ID는 필수입니다.")
    Long bookId,

    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다.")
    int quantity
) {
}
