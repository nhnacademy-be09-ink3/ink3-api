package shop.ink3.api.order.packaging.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.packaging.entity.Packaging;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PackagingResponse {
    private Long id;
    private String name;
    private Integer price;
    private Boolean isAvailable;

    public static PackagingResponse from(Packaging packaging) {
        return new PackagingResponse(
                packaging.getId(),
                packaging.getName(),
                packaging.getPrice(),
                packaging.getIsAvailable()
        );
    }
}
