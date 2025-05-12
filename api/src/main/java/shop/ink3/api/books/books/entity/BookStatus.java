package shop.ink3.api.books.books.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookStatus {
    AVAILABLE("판매 중"),
    OUT_OF_STOCK("수량 부족"),
    SOLD_OUT("판매 종료"),
    DELETED("삭제");

    private final String label;
}
