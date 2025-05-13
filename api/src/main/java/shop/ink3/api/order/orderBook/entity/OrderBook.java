package shop.ink3.api.order.orderBook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.packaging.entity.Packaging;
import shop.ink3.api.review.entity.Review;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "order_books")
public class OrderBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "packaging_id", nullable = true)
    private Packaging packaging;

    @ManyToOne
    @JoinColumn(name = "coupon_store_id", nullable = true)
    private CouponStore couponStore;

    @OneToOne(mappedBy = "orderBook")
    private Review review;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}