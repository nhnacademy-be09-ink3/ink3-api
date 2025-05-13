package shop.ink3.api.user.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(
        name = "admins",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_admin_login_id", columnNames = "login_id"),
        }
)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String loginId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdminStatus status;

    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void update(String name) {
        this.name = name;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void activate() {
        this.status = AdminStatus.ACTIVE;
    }

    public void markAsDormant() {
        this.status = AdminStatus.DORMANT;
    }

    public void withdraw() {
        this.status = AdminStatus.WITHDRAWN;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
