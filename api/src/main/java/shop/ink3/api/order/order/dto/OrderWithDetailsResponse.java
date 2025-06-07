package shop.ink3.api.order.order.dto;

import java.time.LocalDateTime;
import shop.ink3.api.order.order.entity.OrderStatus;

public interface OrderWithDetailsResponse {
    Long getId();
    String getOrderUUID();
    OrderStatus getStatus();
    LocalDateTime getOrderedAt();
    String getOrdererName();
    String getOrdererPhone();
    Integer getPaymentAmount();
    String getRepresentativeBookName();
    String getRepresentativeThumbnailUrl();
    Integer getBookTypeCount();
    Long getOrderBookId();
    Long getBookId();
    Long getHasReview();
}
