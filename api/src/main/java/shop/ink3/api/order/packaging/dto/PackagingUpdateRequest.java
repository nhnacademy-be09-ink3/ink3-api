package shop.ink3.api.order.packaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PackagingUpdateRequest {
    @NotBlank
    @Length(max = 20)
    private String name;
    @NotNull
    private Integer price;
}
