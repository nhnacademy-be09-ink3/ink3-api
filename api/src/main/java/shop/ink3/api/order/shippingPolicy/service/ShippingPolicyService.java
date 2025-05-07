package shop.ink3.api.order.shippingPolicy.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyCreateRequest;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyResponse;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyUpdateRequest;
import shop.ink3.api.order.shippingPolicy.entity.ShippingPolicy;
import shop.ink3.api.order.shippingPolicy.exception.ShippingPolicyNotFoundException;
import shop.ink3.api.order.shippingPolicy.repository.ShippingPolicyRepository;

@RequiredArgsConstructor
@Service
public class ShippingPolicyService {

    private final ShippingPolicyRepository shippingPolicyRepository;

    // 생성
    @Transactional
    public ShippingPolicyResponse createShippingPolicy(ShippingPolicyCreateRequest request) {
        ShippingPolicy shippingPolicy = ShippingPolicy.builder()
                .name(request.getName())
                .threshold(request.getThreshold())
                .fee(request.getFee())
                .isAvailable(false)
                .createdAt(LocalDateTime.now())
                .build();

        return ShippingPolicyResponse.from(shippingPolicyRepository.save(shippingPolicy));
    }

    // 조회
    public ShippingPolicyResponse getShippingPolicy(long shippingPolicyId) {
        Optional<ShippingPolicy> optionalShippingPolicy = shippingPolicyRepository.findById(shippingPolicyId);
        if (!optionalShippingPolicy.isPresent()) {
            throw new ShippingPolicyNotFoundException(shippingPolicyId);
        }
        return ShippingPolicyResponse.from(optionalShippingPolicy.get());
    }

    // 전체 정책 list 조회
    public PageResponse<ShippingPolicyResponse> getShippingPolicyList(Pageable pageable) {
        Page<ShippingPolicy> page = shippingPolicyRepository.findAll(pageable);
        Page<ShippingPolicyResponse> pageResponse = page.map(
                shippingPolicy -> ShippingPolicyResponse.from(shippingPolicy));
        return PageResponse.from(pageResponse);
    }

    // 활성화된 정책 조회
    public ShippingPolicyResponse getActivateShippingPolicy() {
        Optional<ShippingPolicy> optionalShippingPolicy = shippingPolicyRepository.findByIsAvailableTrue();
        if (!optionalShippingPolicy.isPresent()) {
            throw new ShippingPolicyNotFoundException();
        }
        return ShippingPolicyResponse.from(optionalShippingPolicy.get());
    }

    // 수정
    @Transactional
    public ShippingPolicyResponse updateShippingPolicy(long shippingPolicyId, ShippingPolicyUpdateRequest request) {
        Optional<ShippingPolicy> optionalShippingPolicy = shippingPolicyRepository.findById(shippingPolicyId);
        if (!optionalShippingPolicy.isPresent()) {
            throw new ShippingPolicyNotFoundException(shippingPolicyId);
        }
        ShippingPolicy shippingPolicy = optionalShippingPolicy.get();
        shippingPolicy.update(request);

        return ShippingPolicyResponse.from(shippingPolicyRepository.save(shippingPolicy));
    }

    // 삭제
    @Transactional
    public void deleteShippingPolicy(long shippingPolicyId) {
        Optional<ShippingPolicy> optionalShippingPolicy = shippingPolicyRepository.findById(shippingPolicyId);
        if (!optionalShippingPolicy.isPresent()) {
            throw new ShippingPolicyNotFoundException(shippingPolicyId);
        }
        shippingPolicyRepository.deleteById(shippingPolicyId);
    }


    // 특정 배송정책 비활성화
    @Transactional
    public void deactivate(long shippingPolicyId) {
        Optional<ShippingPolicy> optionalShippingPolicy = shippingPolicyRepository.findById(shippingPolicyId);
        if (!optionalShippingPolicy.isPresent()) {
            throw new ShippingPolicyNotFoundException();
        }

        ShippingPolicy shippingPolicy = optionalShippingPolicy.get();
        shippingPolicy.deactivate();
        shippingPolicyRepository.save(shippingPolicy);
    }

    // 특정 배송정책 활성화
    @Transactional
    public void activate(long shippingPolicyId) {
        Optional<ShippingPolicy> optionalShippingPolicy = shippingPolicyRepository.findById(shippingPolicyId);
        if (!optionalShippingPolicy.isPresent()) {
            throw new ShippingPolicyNotFoundException();
        }

        ShippingPolicy shippingPolicy = optionalShippingPolicy.get();
        shippingPolicy.activate();
        shippingPolicyRepository.save(shippingPolicy);
    }
}
