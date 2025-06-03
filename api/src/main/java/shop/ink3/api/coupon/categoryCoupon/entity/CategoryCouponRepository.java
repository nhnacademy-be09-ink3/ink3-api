package shop.ink3.api.coupon.categoryCoupon.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {

    @Query("select cc.id from CategoryCoupon cc where cc.category.id = :categoryId")
    List<Long> findIdsByCategoryId(@Param("categoryId") Long categoryId);

    List<CategoryCoupon> findAllByCategoryId(Long categoryId);
}

