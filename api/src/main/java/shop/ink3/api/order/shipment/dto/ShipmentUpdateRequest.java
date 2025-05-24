package shop.ink3.api.order.shipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ShipmentUpdateRequest {
    @NotBlank
    @Length(max = 50)
    private String recipientName;
    @NotBlank
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
    private String recipientPhone;
    @NotNull
    private Integer postalCode;
    @NotBlank
    @Length(max = 100)
    private String defaultAddress;
    @Length(max = 100)
    @NotBlank
    private String detailAddress;
    @Length(max = 100)
    private String extraAddress;
    @NotNull
    private Integer shippingFee;
    @Length(max = 20)
    private String shippingCode;
}
