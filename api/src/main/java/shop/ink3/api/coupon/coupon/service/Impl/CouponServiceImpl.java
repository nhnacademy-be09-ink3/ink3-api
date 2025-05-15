package shop.ink3.api.coupon.coupon.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.bookCoupon.entity.dto.BookCouponCreateRequest;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.dto.CategoryCouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponUpdateRequest;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.coupon.service.CouponService;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.repository.UserCouponRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponStore;
    private final PolicyRepository policyRepository;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponCreateRequest req) {
        CouponPolicy policy = policyRepository.findById(req.policyId())
                .orElseThrow(() -> new PolicyNotFoundException("정책 없음"));

        Coupon coupon = Coupon.builder()
                .couponName(req.couponName())
                .couponCode(req.CouponCode())
                .couponPolicy(policy)
                .triggerType(req.triggerType())
                .issueType(req.issueType())
                .expiredDate(req.expirationDate())
                .build();

        couponRepository.save(coupon);

        List<CouponResponse.BookInfo> bookInfos = new ArrayList<>();
        if (req.books() != null) {
            for (BookCouponCreateRequest bookDto : req.books()) {
                Book book = bookDto.book();
                bookCouponRepository.save(new BookCoupon(book, coupon));
                bookInfos.add(new CouponResponse.BookInfo(book.getId(), book.getTitle())); // 또는 getName()
            }
        }

        List<CouponResponse.CategoryInfo> categoryInfos = new ArrayList<>();
        if (req.categories() != null) {
            for (CategoryCouponCreateRequest categoryDto : req.categories()) {
                Category category = categoryDto.category();
                categoryCouponRepository.save(new CategoryCoupon(category, coupon));
                categoryInfos.add(new CouponResponse.CategoryInfo(category.getId(), category.getName()));
            }
        }

        return new CouponResponse(
                coupon.getId(),
                coupon.getCouponPolicy().getId(),
                coupon.getCouponName(),
                coupon.getTriggerType(),
                coupon.getIssueType(),
                coupon.getCouponCode(),
                coupon.getExpiredDate(),
                bookInfos,
                categoryInfos
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponByTriggerType(TriggerType triggerType) {
        List<Coupon> coupons = couponRepository.findByTriggerType(triggerType);

        return coupons.stream()
                .map(coupon -> {
                    List<BookCoupon> bookCoupons = bookCouponRepository.findByCouponId(coupon.getId());
                    List<CategoryCoupon> categoryCoupons = categoryCouponRepository.findByCouponId(coupon.getId());
                    return CouponResponse.from(coupon, bookCoupons, categoryCoupons);
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponByIssueType(IssueType issueType) {
        List<Coupon> coupons = couponRepository.findByIssueType(issueType);

        return coupons.stream()
                .map(coupon -> {
                    List<BookCoupon> bookCoupons = bookCouponRepository.findByCouponId(coupon.getId());
                    List<CategoryCoupon> categoryCoupons = categoryCouponRepository.findByCouponId(coupon.getId());
                    return CouponResponse.from(coupon, bookCoupons, categoryCoupons);
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(()->new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        List<BookCoupon> bookCoupons = bookCouponRepository.findByCouponId(coupon.getId());
        List<CategoryCoupon> categoryCoupons = categoryCouponRepository.findByCouponId(coupon.getId());
        return CouponResponse.from(coupon, bookCoupons, categoryCoupons);
    }

    @Override
    public CouponResponse getCouponByName(String couponName) {
        Coupon coupon = couponRepository.findByCouponName(couponName).orElseThrow(()->new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        List<BookCoupon> bookCoupons = bookCouponRepository.findByCouponId(coupon.getId());
        List<CategoryCoupon> categoryCoupons = categoryCouponRepository.findByCouponId(coupon.getId());
        return CouponResponse.from(coupon, bookCoupons, categoryCoupons);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();

        return coupons.stream()
                .map(coupon -> {
                    List<BookCoupon> bookCoupons = bookCouponRepository.findByCouponId(coupon.getId());
                    List<CategoryCoupon> categoryCoupons = categoryCouponRepository.findByCouponId(coupon.getId());

                    return CouponResponse.from(coupon, bookCoupons, categoryCoupons);
                })
                .collect(Collectors.toList());
    }


    @Override
    public CouponResponse updateCoupon(CouponUpdateRequest coupon) {
        return null;
    }

    @Override
    public void deleteCoupon(String couponId) {

    }

    @Override
    public void deleteCouponByName(String couponName) {

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
        List<CouponStore> stores = userCouponStore.findByUserId(userId);

        return stores.stream()
                .map(store -> new CouponStoreResponse(
                        store.getId(),
                        store.getCoupon().getCouponName(),
                        store.getCoupon().getCouponCode(), // 코드가 없을 경우 null 가능
                        store.getCreatedAt(),
                        store.getUsedAt(),
                        store.getValidFrom(),
                        store.getValidUntil(),
                        store.isUsed()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void useCoupon(Long userCouponId, String orderId) {

    }

    @Override
    public void cancelCouponUse(Long userCouponId) {

    }
}
