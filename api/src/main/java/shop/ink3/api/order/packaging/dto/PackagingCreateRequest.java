package shop.ink3.api.order.packaging.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PackagingCreateRequest {
    @NotBlank
    @Length(max = 20)
    private String name;
    @NotBlank
    private Integer price;
}
