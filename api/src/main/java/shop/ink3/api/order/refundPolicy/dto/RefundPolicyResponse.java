package shop.ink3.api.order.refundPolicy.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.refundPolicy.entity.RefundPolicy;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefundPolicyResponse {
    private Long id;
    private String name;
    private Integer returnDeadLine;
    private Integer defectReturnDeadLine;
    private Boolean isAvailable;
    private LocalDateTime createdAt;


    public static RefundPolicyResponse from(RefundPolicy refundPolicy) {
        return new RefundPolicyResponse(
                refundPolicy.getId(),
                refundPolicy.getName(),
                refundPolicy.getReturnDeadLine(),
                refundPolicy.getDefectReturnDeadLine(),
                refundPolicy.getIsAvailable(),
                refundPolicy.getCreatedAt()
        );
    }
}
