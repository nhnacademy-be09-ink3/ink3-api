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
        ShippingPolicy shippingPolicy = getShippingPolicyOrThrow(shippingPolicyId);
        return ShippingPolicyResponse.from(shippingPolicy);
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
        ShippingPolicy shippingPolicy = getShippingPolicyOrThrow(shippingPolicyId);
        shippingPolicy.update(request);
        return ShippingPolicyResponse.from(shippingPolicyRepository.save(shippingPolicy));
    }

    // 삭제
    @Transactional
    public void deleteShippingPolicy(long shippingPolicyId) {
        getShippingPolicyOrThrow(shippingPolicyId);
        shippingPolicyRepository.deleteById(shippingPolicyId);
    }


    // 특정 배송정책 비활성화
    @Transactional
    public void deactivate(long shippingPolicyId) {
        ShippingPolicy shippingPolicy = getShippingPolicyOrThrow(shippingPolicyId);
        shippingPolicy.deactivate();
        shippingPolicyRepository.save(shippingPolicy);
    }

    // 특정 배송정책 활성화
    @Transactional
    public void activate(long shippingPolicyId) {
        ShippingPolicy shippingPolicy = getShippingPolicyOrThrow(shippingPolicyId);
        shippingPolicy.activate();
        shippingPolicyRepository.save(shippingPolicy);
    }

    // 조회 로직
    private ShippingPolicy getShippingPolicyOrThrow(long shippingPolicyId) {
        Optional<ShippingPolicy> optionalShippingPolicy = shippingPolicyRepository.findById(shippingPolicyId);
        if (!optionalShippingPolicy.isPresent()) {
            throw new ShippingPolicyNotFoundException();
        }
        return optionalShippingPolicy.get();
    }

    // 주문 금액에 따른 배송비
    public Integer getShippingFee(int orderPrice){
        ShippingPolicyResponse response = getActivateShippingPolicy();
        if(response.getThreshold() > orderPrice){
            return response.getFee();
        }else{
            return 0;
        }
    }
}
