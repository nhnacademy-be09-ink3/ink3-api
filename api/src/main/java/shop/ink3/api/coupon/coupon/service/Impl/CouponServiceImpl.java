package shop.ink3.api.coupon.coupon.service.Impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.BookInfo;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.CategoryInfo;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.coupon.service.CouponService;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.repository.UserCouponRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponStore;
    private final PolicyRepository policyRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public CouponResponse createCoupon(CouponCreateRequest req) {
        Coupon coupon = Coupon.builder()
                .name(req.name())
                .issueType(req.issueType())
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
                .map(bc -> new CouponResponse.BookInfo(bc.getBook().getId(), bc.getBook().getTitle()))
                .toList();
        List<CategoryInfo> categories = coupon.getCategoryCoupons().stream()
                .map(cc -> new CouponResponse.CategoryInfo(cc.getCategory().getId(), cc.getCategory().getName()))
                .toList();

        return CouponResponse.from(coupon, books, categories);
    }

    private List<CouponResponse> getCouponResponses(List<Coupon> coupons) {
        return coupons.stream()
                .map(this::getCouponResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponByIssueType(IssueType issueType) {
        List<Coupon> coupons = couponRepository.findAllByIssueTypeWithFetch(issueType)
                .orElseThrow(() -> new CouponNotFoundException(issueType.name()));

        return getCouponResponses(coupons);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(long id) {
        Coupon coupon = couponRepository.findByIdWithFetch(id)
                .orElseThrow(() -> new CouponNotFoundException(id + " 쿠폰을 찾을 수 없습니다."));

        return getCouponResponse(coupon);
    }

    private CouponResponse getCouponResponse(Coupon coupon) {
        Set<BookCoupon> bookCoupons = coupon.getBookCoupons();
        Set<CategoryCoupon> categoryCoupons = coupon.getCategoryCoupons();

        List<BookInfo> bookIds = bookCoupons.stream()
                .map(bc -> new CouponResponse.BookInfo(
                        bc.getBook().getId(),
                        bc.getBook().getTitle()))
                .collect(Collectors.toList());

        List<CategoryInfo> categoryIds = categoryCoupons.stream()
                .map(cc -> new CouponResponse.CategoryInfo(
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

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAllWithAssociations();
        return getCouponResponses(coupons);
    }

    @Override
    public void deleteCouponById(Long couponId) {
        couponRepository.deleteById(couponId);
    }

    @Override
    public void issueBookCoupons(Long couponId, Long bookId) {

    }

    @Override
    public void issueCategoryCoupons(Long couponId, Long categoryId) {

    }

    @Transactional
    public List<Long> issueBirthdayCoupons(List<Long> userIds, Long couponId, LocalDate issuedDate) {
        int year = issuedDate.getYear();

        List<Long> issuedUsers = new ArrayList<>();
        List<CouponStore> couponsToIssue = new ArrayList<>();

        for (Long userId : userIds) {
            boolean alreadyIssued = userCouponStore.existsByUserIdAndCouponIdAndYear(userId, couponId, year);

            if (!alreadyIssued) {
                User user = userRepository.getReferenceById(userId);
                Coupon coupon = couponRepository.getReferenceById(couponId);
                couponsToIssue.add(CouponStore.builder()
                        .user(user)
                        .coupon(coupon)
                        .status(CouponStatus.READY)
                        .issuedAt(LocalDateTime.now())
                        .build());
                issuedUsers.add(userId);
            }
        }

        userCouponStore.saveAll(couponsToIssue);
        return issuedUsers;
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
