package shop.ink3.api.coupon.coupon.service.Impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.coupon.service.CouponService;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;
import shop.ink3.api.coupon.store.repository.UserCouponRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponStore;
    private final PolicyRepository policyRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Override
    public CouponResponse createCoupon(CouponCreateRequest req) {

        Coupon coupon = Coupon.builder()
                .couponName(req.couponName())
                .triggerType(req.triggerType())
                .issueType(req.issueType())
                .couponCode(req.CouponCode())
                .couponPolicy(policyRepository.findById(req.policyId()).orElse(null))
                .issueDate(req.issueDate())
                .expiredDate(req.expirationDate())
                .build();
        if(req.triggerType() == TriggerType.BOOK) {
            List<Book> books = bookRepository.findAllById(req.bookIdList());
            coupon.addBookCoupon(books);
        } else if(req.triggerType() == TriggerType.CATEGORY) {
            List<Category>categories = categoryRepository.findAllById(req.categoryIdList());
            coupon.addCategoryCoupon(categories);
        }

        couponRepository.save(coupon);
        return CouponResponse.from(coupon, req.bookIdList(), req.categoryIdList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponByTriggerType(TriggerType triggerType) {
        List<Coupon> coupons = couponRepository.findAllByTriggerType(triggerType).orElseThrow(()->new CouponNotFoundException(triggerType.name()));

        return getCouponResponses(coupons);
    }

    private List<CouponResponse> getCouponResponses(List<Coupon> coupons) {
        return coupons.stream()
                .map(this::getCouponResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponByIssueType(IssueType issueType) {
        List<Coupon> coupons = couponRepository.findAllByIssueType(issueType).orElseThrow(()->new CouponNotFoundException(issueType.name()));

        return getCouponResponses(coupons);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CouponNotFoundException(id + " 쿠폰을 찾을 수 없습니다."));

        return getCouponResponse(coupon);
    }

    private CouponResponse getCouponResponse(Coupon coupon) {
        List<BookCoupon> bookCoupons = coupon.getBookCoupons();
        List<CategoryCoupon> categoryCoupons = coupon.getCategoryCoupons();

        List<Long> bookIds = bookCoupons.stream()
                .map(bc -> bc.getBook().getId())
                .collect(Collectors.toList());

        List<Long> categoryIds = categoryCoupons.stream()
                .map(cc -> cc.getCategory().getId())
                .collect(Collectors.toList());
        return CouponResponse.from(coupon, bookIds, categoryIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponByCouponName(String couponName) {
        List<Coupon> coupons = couponRepository.findAllByCouponName(couponName);
        if (coupons.isEmpty()) {
            throw new CouponNotFoundException(couponName + " 쿠폰을 찾을 수 없습니다.");
        }
        return getCouponResponses(coupons);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();

        return getCouponResponses(coupons);
    }

    @Override
    public void deleteCouponById(Long couponId) {
        // 존재 여부 체크 등 필요 시 추가
        couponRepository.deleteById(couponId);
    }

    @Override
    public void deleteCouponByName(String couponName) {
        // 존재 여부 체크 등 필요 시 추가
        couponRepository.deleteByCouponName(couponName);
    }


    @Override
    public void issueBookCoupons(Long couponId, Long bookId) {

    }

    @Override
    public void issueCategoryCoupons(Long couponId, Long categoryId) {

    }

    @Override
    public void issueCouponByCode(Long userId, String couponCode) {

    }

    @Override
    public void issueCouponById(Long userId, Long couponId){

    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponStoreResponse> getUserCoupons(Long userId) {
        return null;
    }

    @Override
    public void useCoupon(Long userCouponId, String orderId) {

    }

    @Override
    public void cancelCouponUse(Long userCouponId) {

    }
}
