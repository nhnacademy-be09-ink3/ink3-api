package shop.ink3.api.review.review.dto;

public enum ReviewPointType {
    REVIEW(200),
    REVIEW_IMAGE(500),
    ;

    private final Integer amount;

    ReviewPointType(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}
