package shop.ink3.api.coupon.categoryCoupon.entity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {

    @Query("select cc.id from CategoryCoupon cc where cc.category.id = :categoryId")
    List<Long> findIdsByCategoryId(@Param("categoryId") Long categoryId);

    List<CategoryCoupon> findAllByCategoryId(Long categoryId);

    /**
     * 단일 CategoryCoupon을 조회할 때, 연관된 category와 coupon을
     * 한 번의 쿼리로 모두 fetch join하여 가져온다.
     */
    @Query("""
        SELECT cc
        FROM CategoryCoupon cc
        JOIN FETCH cc.category
        JOIN FETCH cc.coupon
        WHERE cc.id = :id
    """)
    Optional<CategoryCoupon> findByIdWithCategoryAndCoupon(@Param("id") Long id);


    /**
     * 여러 CategoryCoupon을 조회할 때도 fetch join이 필요하다면,
     * 예를 들어 카테고리 ID 목록으로 조회하고 싶을 때:
     */
    @Query("""
        SELECT cc
        FROM CategoryCoupon cc
        JOIN FETCH cc.category
        JOIN FETCH cc.coupon
        WHERE cc.category.id IN :categoryIds
    """)
    List<CategoryCoupon> findAllByCategoryIdInWithFetch(@Param("categoryIds") Collection<Long> categoryIds);

}

