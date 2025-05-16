package shop.ink3.api.order.refundPolicy.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyCreateRequest;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyResponse;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyUpdateRequest;
import shop.ink3.api.order.refundPolicy.entity.RefundPolicy;
import shop.ink3.api.order.refundPolicy.exception.RefundPolicyNotFoundException;
import shop.ink3.api.order.refundPolicy.repository.RefundPolicyRepository;
import shop.ink3.api.order.shippingPolicy.entity.ShippingPolicy;
import shop.ink3.api.order.shippingPolicy.exception.ShippingPolicyNotFoundException;

@RequiredArgsConstructor
@Service
public class RefundPolicyService {

    private final RefundPolicyRepository refundPolicyRepository;

    // 생성
    @Transactional
    public RefundPolicyResponse createRefundPolicy(RefundPolicyCreateRequest request) {
        RefundPolicy refundPolicy = RefundPolicy.builder()
                .name(request.getName())
                .returnDeadLine(request.getReturnDeadLine())
                .defectReturnDeadLine(request.getDefectReturnDeadLine())
                .isAvailable(false)
                .createdAt(LocalDateTime.now())
                .build();

        return RefundPolicyResponse.from(refundPolicyRepository.save(refundPolicy));
    }

    // 조회
    public RefundPolicyResponse getRefundPolicy(long refundPolicyId) {
        RefundPolicy refundPolicy = getRefundPolicyOrThrow(refundPolicyId);
        return RefundPolicyResponse.from(refundPolicy);
    }

    // 전체 정책 list 조회
    public PageResponse<RefundPolicyResponse> getRefundPolicyList(Pageable pageable) {
        Page<RefundPolicy> page = refundPolicyRepository.findAll(pageable);
        Page<RefundPolicyResponse> responsePage = page.map(refundPolicy ->
                RefundPolicyResponse.from(refundPolicy));
        return PageResponse.from(responsePage);
    }

    // 활성화된 정책 list 조회
    public PageResponse<RefundPolicyResponse> getAvailableRefundPolicyList(Pageable pageable) {
        Page<RefundPolicy> page = refundPolicyRepository.findByIsAvailableTrue(pageable);
        Page<RefundPolicyResponse> responsePage = page.map(refundPolicy ->
                RefundPolicyResponse.from(refundPolicy));
        return PageResponse.from(responsePage);
    }

    // 수정
    @Transactional
    public RefundPolicyResponse updateRefundPolicy(long refundPolicyId, RefundPolicyUpdateRequest request) {
        RefundPolicy refundPolicy = getRefundPolicyOrThrow(refundPolicyId);
        refundPolicy.update(request);
        return RefundPolicyResponse.from(refundPolicy);
    }

    // 삭제
    @Transactional
    public void deleteRefundPolicy(long refundPolicyId) {
        getRefundPolicyOrThrow(refundPolicyId);
        refundPolicyRepository.deleteById(refundPolicyId);
    }

    // 활성화
    @Transactional
    public void activate(long refundPolicyId) {
        RefundPolicy refundPolicy = getRefundPolicyOrThrow(refundPolicyId);
        refundPolicy.activate();
        refundPolicyRepository.save(refundPolicy);
    }

    // 비활성화
    @Transactional
    public void deactivate(long refundPolicyId) {
        RefundPolicy refundPolicy = getRefundPolicyOrThrow(refundPolicyId);
        refundPolicy.deactivate();
        refundPolicyRepository.save(refundPolicy);
    }



    // 조회 로직
    private RefundPolicy getRefundPolicyOrThrow(long refundPolicyId) {
        Optional<RefundPolicy> optionalRefundPolicy = refundPolicyRepository.findById(refundPolicyId);
        if (!optionalRefundPolicy.isPresent()) {
            throw new RefundPolicyNotFoundException(refundPolicyId);
        }
        return optionalRefundPolicy.get();
    }
}
