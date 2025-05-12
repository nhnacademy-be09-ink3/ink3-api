package shop.ink3.api.coupon.bookCoupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.books.books.entity.Books;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.order.order.entity.Order;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "book_coupons")
public class BookCoupon {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "book_coupon_id")
    private Coupon coupon;

    @OneToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Books books;
}
