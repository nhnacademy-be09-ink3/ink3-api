package shop.ink3.api.order.guest.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.ink3.api.order.guest.dto.GuestOrderDetailsResponse;
import shop.ink3.api.order.guest.entiity.Guest;

public interface GuestOrderRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByOrderIdAndEmail(long orderId ,String email);

    void deleteByOrderId(Long orderId);

    Optional<Guest> findByOrderId(long orderId);

    @Query(value = """
    SELECT\s
        o.id AS orderId,
        o.order_uuid AS orderUUId,
        o.status AS status,
        o.ordered_at AS orderedAt,
        o.orderer_name AS ordererName,
        o.orderer_phone AS ordererPhone,
        s.preferred_delivery_date AS preferredDeliveryDate,
        s.delivered_at AS deliveredAt,
        s.recipient_name AS recipientName,
        s.recipient_phone AS recipientPhone,
        s.postal_code AS postalCode,
        s.default_address AS defaultAddress,
        s.extra_address AS extraAddress,
        s.shipping_fee AS shippingFee,
        s.shipping_code AS shippingCode
    FROM orders o
    JOIN shipments s ON s.order_id = o.id
    WHERE o.id = :orderId
   \s""",
            nativeQuery = true)
    Optional<GuestOrderDetailsResponse> findByGuestOrderDetails(long orderId);
}
