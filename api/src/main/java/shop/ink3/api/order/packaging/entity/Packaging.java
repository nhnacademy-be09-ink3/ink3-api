package shop.ink3.api.order.packaging.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.packaging.dto.PackagingUpdateRequest;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "packagings")
public class Packaging {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Boolean isAvailable;

    public void activate() {
        this.isAvailable = true;
    }

    public void deactivate() {
        this.isAvailable = false;
    }

    public void update(PackagingUpdateRequest request) {
        this.name = request.getName();
        this.price = request.getPrice();
    }
}
