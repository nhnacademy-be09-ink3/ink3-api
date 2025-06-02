package shop.ink3.api.coupon.coupon.service.Impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.BookInfo;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.CategoryInfo;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.coupon.service.CouponService;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;


@Transactional
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final PolicyRepository policyRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    /**
     * Creates a new coupon with the specified properties and associations.
     *
     * Builds a coupon entity from the provided request, associates it with books and categories if specified,
     * validates the existence of referenced books and categories, and saves the coupon. Returns a response DTO
     * containing the created coupon and its associated books and categories.
     *
     * @param req the request containing coupon details and optional book and category associations
     * @return a response representing the created coupon with associated books and categories
     * @throws IllegalArgumentException if any provided book or category ID does not exist
     */
    @Override
    public CouponResponse createCoupon(CouponCreateRequest req) {
        Coupon coupon = Coupon.builder()
                .name(req.name())
                .couponPolicy(policyRepository.findById(req.policyId()).orElse(null))
                .issuableFrom(req.issuableFrom())
                .expiresAt(req.expiresAt())
                .build();

        if (!req.bookIdList().isEmpty()) {
            List<Book> books = bookRepository.findAllById(req.bookIdList());
            if (books.size() != req.bookIdList().size()) {
                throw new IllegalArgumentException("존재하지 않는 book ID가 포함되어 있습니다.");
            }
            coupon.addBookCoupon(books);
        }

        if (!req.categoryIdList().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(req.categoryIdList());
            if (categories.size() != req.categoryIdList().size()) {
                throw new IllegalArgumentException("존재하지 않는 category ID가 포함되어 있습니다.");
            }
            coupon.addCategoryCoupon(categories);
        }

        couponRepository.save(coupon);
        List<BookInfo> books = coupon.getBookCoupons().stream()
                .map(bc -> new CouponResponse.BookInfo(bc.getId(), bc.getBook().getId(), bc.getBook().getTitle()))
                .toList();
        List<CategoryInfo> categories = coupon.getCategoryCoupons().stream()
                .map(cc -> new CouponResponse.CategoryInfo(cc.getId(), cc.getCategory().getId(), cc.getCategory().getName()))
                .toList();

        return CouponResponse.from(coupon, books, categories);
    }

    /**
     * Converts a list of Coupon entities into a list of CouponResponse DTOs.
     *
     * @param coupons the list of Coupon entities to convert
     * @return a list of CouponResponse objects representing the provided coupons
     */
    private List<CouponResponse> getCouponResponses(List<Coupon> coupons) {
        return coupons.stream()
                .map(this::getCouponResponse)
                .collect(Collectors.toList());
    }

    /****
     * Retrieves a coupon by its ID and returns its details.
     *
     * @param id the unique identifier of the coupon to retrieve
     * @return a CouponResponse containing the coupon's information
     * @throws CouponNotFoundException if no coupon with the specified ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(long id) {
        Coupon coupon = couponRepository.findByIdWithFetch(id)
                .orElseThrow(() -> new CouponNotFoundException(id + " 쿠폰을 찾을 수 없습니다."));

        return getCouponResponse(coupon);
    }

    /**
     * Converts a Coupon entity into a CouponResponse DTO, including associated books and categories.
     *
     * @param coupon the Coupon entity to convert
     * @return a CouponResponse containing coupon details and lists of associated books and categories
     */
    private CouponResponse getCouponResponse(Coupon coupon) {
        Set<BookCoupon> bookCoupons = coupon.getBookCoupons();
        Set<CategoryCoupon> categoryCoupons = coupon.getCategoryCoupons();

        List<BookInfo> bookIds = bookCoupons.stream()
                .map(bc -> new CouponResponse.BookInfo(
                        bc.getId(),
                        bc.getBook().getId(),
                        bc.getBook().getTitle()))
                .collect(Collectors.toList());

        List<CategoryInfo> categoryIds = categoryCoupons.stream()
                .map(cc -> new CouponResponse.CategoryInfo(
                        cc.getId(),
                        cc.getCategory().getId(),
                        cc.getCategory().getName()))
                .collect(Collectors.toList());
        return CouponResponse.from(coupon, bookIds, categoryIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponByName(String couponName) {
        List<Coupon> coupons = couponRepository.findAllByNameWithFetch(couponName).orElseThrow(
                ()-> new CouponNotFoundException(couponName + " not found")
        );
        if (coupons.isEmpty()) {
            throw new CouponNotFoundException(couponName + " 쿠폰을 찾을 수 없습니다.");
        }
        return getCouponResponses(coupons);
    }

    /**
     * Retrieves all coupons along with their associated books and categories.
     *
     * @return a list of CouponResponse DTOs representing all coupons and their associations
     */
    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAllWithAssociations();
        return getCouponResponses(coupons);
    }


    /****
     * Deletes a coupon by its unique identifier.
     *
     * @param couponId the ID of the coupon to delete
     */
    @Override
    public void deleteCouponById(Long couponId) {
        couponRepository.deleteById(couponId);
    }

}
