package shop.ink3.api.coupon.coupon.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.category.entity.Category;
import shop.ink3.api.book.category.exception.CategoryNotFoundException;
import shop.ink3.api.book.category.repository.CategoryRepository;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.bookCoupon.entity.BookCouponRepository;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCouponRepository;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.BookInfo;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.CategoryInfo;
import shop.ink3.api.coupon.coupon.dto.CouponUpdateRequest;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.exception.BusinessException;
import shop.ink3.api.coupon.coupon.exception.CouponInUseException;
import shop.ink3.api.coupon.coupon.exception.CouponNotFoundException;
import shop.ink3.api.coupon.coupon.repository.CouponRepository;
import shop.ink3.api.coupon.coupon.service.CouponService;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;
import shop.ink3.api.coupon.store.repository.CouponStoreRepository;


@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final PolicyRepository policyRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;
    private final CouponStoreRepository couponStoreRepository;

    @Override
    public CouponResponse createCoupon(CouponCreateRequest req) {

        if (req.expiresAt().isBefore(req.issuableFrom())) {
            throw new BusinessException("만료일(expiresAt)은 발행시작일(issuableFrom) 이후여야 합니다.");
        }

        Coupon coupon = Coupon.builder()
                .name(req.name())
                .couponPolicy(policyRepository.findById(req.policyId()).orElseThrow(()->new PolicyNotFoundException("없는 정책입니다.")))
                .issuableFrom(req.issuableFrom())
                .expiresAt(req.expiresAt())
                .createdAt(LocalDateTime.now())
                .build();

        if (!req.bookIdList().isEmpty()) {
            List<Book> books = bookRepository.findAllById(req.bookIdList());
            if (books.size() != req.bookIdList().size()) {
                log.info("넘어온 bookIdList = {}", req.bookIdList());
                log.info("Book: {}",books);
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
                .map(bc -> new CouponResponse.BookInfo(bc.getId(), bc.getBook().getId(), bc.getBook().getTitle(), "BOOK"))
                .toList();
        List<CategoryInfo> categories = coupon.getCategoryCoupons().stream()
                .map(cc -> new CouponResponse.CategoryInfo(cc.getId(), cc.getCategory().getId(), cc.getCategory().getName(), "CATEGORY"))
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
    public CouponResponse getCouponById(long id) {
        Coupon coupon = couponRepository.findByIdWithFetch(id)
                .orElseThrow(() -> new CouponNotFoundException(id + " 쿠폰을 찾을 수 없습니다."));

        return getCouponResponse(coupon);
    }

    private CouponResponse getCouponResponse(Coupon coupon) {
        Set<BookCoupon> bookCoupons = coupon.getBookCoupons();
        Set<CategoryCoupon> categoryCoupons = coupon.getCategoryCoupons();

        List<BookInfo> bookInfos = bookCoupons.stream()
                .map(bc -> new CouponResponse.BookInfo(
                        bc.getId(),
                        bc.getBook().getId(),
                        bc.getBook().getTitle(),
                        "BOOK"))
                .collect(Collectors.toList());

        List<CategoryInfo> categoryInfos = categoryCoupons.stream()
                .map(cc -> new CouponResponse.CategoryInfo(
                        cc.getId(),
                        cc.getCategory().getId(),
                        cc.getCategory().getName(),
                        "CATEGORY"))
                .collect(Collectors.toList());

        return CouponResponse.from(coupon, bookInfos, categoryInfos);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CouponResponse> getAllCoupons(Pageable pageable) {
        Page<Coupon> coupons = couponRepository.findAllWithAssociations(pageable);

        // Page<Coupon> → Page<CouponResponse>
        Page<CouponResponse> dtoPage = coupons.map(this::getCouponResponse);

        // PageResponse.from(Page<CouponResponse>) 사용
        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CouponResponse> getCouponsByBookId(long bookId, Pageable pageable) {
        // 1) 원본 조회
        Page<BookCoupon> bookCoupons = bookCouponRepository.findAllByBookId(bookId, pageable);

        // 2) 만료되지 않은(coupon.expiresAt 이후) 것만 필터링
        LocalDateTime now = LocalDateTime.now();
        List<BookCoupon> validList = bookCoupons.stream()
                .filter(bc -> bc.getCoupon().getIssuableFrom().isBefore(now)
                        || bc.getCoupon().getIssuableFrom().isEqual(now))
                .filter(bc -> bc.getCoupon().getExpiresAt().isAfter(now))
                .toList();

        // 3) 유효 쿠폰이 하나도 없으면 예외
        if (validList.isEmpty()) {
            throw new CouponNotFoundException(bookId + " 북 쿠폰이 존재하지 않습니다.");
        }

        // 4) 필터링된 리스트로 새로운 Page 객체 생성
        Page<BookCoupon> validPage = new PageImpl<>(validList, pageable, validList.size());

        // 5) DTO 로 매핑
        Page<CouponResponse> dtoPage = validPage.map(bc -> {
            BookInfo info = new BookInfo(
                    bc.getId(),
                    bc.getBook().getId(),
                    bc.getBook().getTitle(),
                    "BOOK"
            );
            return CouponResponse.from(bc.getCoupon(), List.of(info), List.of());
        });

        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CouponResponse> getCouponsByCategoryId(long categoryId, Pageable pageable) {
        // 1) 원본 조회
        Page<CategoryCoupon> categoryCoupons =
                categoryCouponRepository.findAllByCategoryId(categoryId, pageable);

        // 2) 만료되지 않은 것만 필터링
        LocalDateTime now = LocalDateTime.now();
        List<CategoryCoupon> validList = categoryCoupons.stream()
                .filter(cc -> cc.getCoupon().getIssuableFrom().isBefore(now)
                        || cc.getCoupon().getIssuableFrom().isEqual(now))

                .filter(cc -> cc.getCoupon().getExpiresAt().isAfter(now))
                .toList();

        // 3) 유효 쿠폰이 하나도 없으면 예외
        if (validList.isEmpty()) {
            throw new CouponNotFoundException(categoryId + " 카테고리 쿠폰이 존재하지 않습니다.");
        }

        // 4) 필터링된 리스트로 새 Page 생성
        Page<CategoryCoupon> validPage =
                new PageImpl<>(validList, pageable, validList.size());

        // 5) DTO 매핑
        Page<CouponResponse> dtoPage = validPage.map(cc -> {
            CategoryInfo info = new CategoryInfo(
                    cc.getId(),
                    cc.getCategory().getId(),
                    cc.getCategory().getName(),
                    "CATEGORY"
            );
            return CouponResponse.from(cc.getCoupon(), List.of(), List.of(info));
        });

        return PageResponse.from(dtoPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<CouponResponse> getCouponsByParentId(long bookId, Pageable pageable) {
        // 1) Book 조회
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));

        // 2) 직접 매핑된 카테고리 ID 수집
        List<Long> directCategoryIds = book.getBookCategories().stream()
                .map(bc -> bc.getCategory().getId())
                .toList();

        // 3) 조상 카테고리 ID 포함
        Set<Long> allCategoryIds = new HashSet<>(directCategoryIds);
        for (Long catId : directCategoryIds) {
            List<Category> ancestors = categoryRepository.findAllAncestors(catId);
            for (Category parent : ancestors) {
                allCategoryIds.add(parent.getId());
            }
        }

        // 4) 부모 카테고리 기준 CategoryCoupon 엔티티 페이징 조회 (fetch join)
        List<CategoryCoupon> page = categoryCouponRepository
                .findAllByCategoryIdInWithFetch(allCategoryIds);

        // 5) 만료 기준 적용
        LocalDateTime now = LocalDateTime.now();
        List<CategoryCoupon> validList = page.stream()
                .filter(cc -> !cc.getCoupon().getIssuableFrom().isAfter(now))  // issuableFrom ≤ now
                .filter(cc -> cc.getCoupon().getExpiresAt().isAfter(now))      // expiresAt > now
                .toList();

        if (validList.isEmpty()) {
            throw new CouponNotFoundException(
                    "상위 카테고리(" + allCategoryIds + ") 기반 쿠폰이 없습니다."
            );
        }

        // 6) 필터된 리스트로 새 Page 생성
        Page<CategoryCoupon> validPage =
                new PageImpl<>(validList, pageable, validList.size());

        // 7) DTO 매핑
        Page<CouponResponse> dtoPage = validPage.map(cc -> {
            CategoryInfo info = new CategoryInfo(
                    cc.getId(),                             // CategoryCoupon PK
                    cc.getCategory().getId(),               // 부모 카테고리 ID
                    cc.getCategory().getName(),             // 부모 카테고리 이름
                    "CATEGORY"                       // originType 표시
            );
            return CouponResponse.from(cc.getCoupon(),
                    List.of(),     // book 기반 리스트 없음
                    List.of(info)); // 부모 카테고리 정보만
        });

        return PageResponse.from(dtoPage);
    }

    @Override
    public CouponResponse updateCoupon(Long couponId, CouponUpdateRequest req) {
        // 1) 기존 Coupon 엔티티 조회
        Coupon coupon = couponRepository.findByIdWithFetch(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId + " 쿠폰을 찾을 수 없습니다."));

        // 2) 요청에서 온 policyId 에 해당하는 정책 가져오기
        CouponPolicy newPolicy = policyRepository.findById(req.policyId())
                .orElseThrow(() -> new PolicyNotFoundException("없는 정책입니다. ID=" + req.policyId()));

        // 3) bookList, categoryList 유효성 검사 및 엔티티 조회
        List<Book> newBookList = List.of();
        if (!req.bookIdList().isEmpty()) {
            newBookList = bookRepository.findAllById(req.bookIdList());
            if (newBookList.size() != req.bookIdList().size()) {
                throw new IllegalArgumentException("존재하지 않는 book ID가 포함되어 있습니다.");
            }
        }

        List<Category> newCategoryList = List.of();
        if (!req.categoryIdList().isEmpty()) {
            newCategoryList = categoryRepository.findAllById(req.categoryIdList());
            if (newCategoryList.size() != req.categoryIdList().size()) {
                throw new IllegalArgumentException("존재하지 않는 category ID가 포함되어 있습니다.");
            }
        }

        coupon.update(
                newPolicy,
                req.name(),
                req.issuableFrom(),
                req.expiresAt(),
                LocalDateTime.now(),
                newBookList,
                newCategoryList
        );

        Coupon saved = couponRepository.save(coupon);

        List<BookInfo> updatedBookInfos = saved.getBookCoupons().stream()
                .map(bc -> new BookInfo(
                        bc.getId(),
                        bc.getBook().getId(),
                        bc.getBook().getTitle(),
                        "BOOK"))
                .collect(Collectors.toList());

        List<CategoryInfo> updatedCategoryInfos = saved.getCategoryCoupons().stream()
                .map(cc -> new CategoryInfo(
                        cc.getId(),
                        cc.getCategory().getId(),
                        cc.getCategory().getName(),
                        "CATEGORY"))
                .collect(Collectors.toList());

        return CouponResponse.from(saved, updatedBookInfos, updatedCategoryInfos);
    }


    @Override
    public void deleteCouponById(Long couponId) {
        boolean issued = couponStoreRepository.existsByCouponId(couponId);
        if (issued) {
            throw new CouponInUseException("이미 사용자에게 발급된 쿠폰이 있어 삭제할 수 없습니다.");
        }
        // 2) 문제 없으면 삭제
        couponRepository.deleteById(couponId);
    }

}
