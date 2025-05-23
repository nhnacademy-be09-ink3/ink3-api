package shop.ink3.api.order.packaging.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.packaging.dto.PackagingCreateRequest;
import shop.ink3.api.order.packaging.dto.PackagingResponse;
import shop.ink3.api.order.packaging.dto.PackagingUpdateRequest;
import shop.ink3.api.order.packaging.entity.Packaging;
import shop.ink3.api.order.packaging.exception.PackagingNotFoundException;
import shop.ink3.api.order.packaging.repository.PackagingRepository;

@RequiredArgsConstructor
@Service
public class PackagingService {

    private final PackagingRepository packagingRepository;

    // 생성
    @Transactional
    public PackagingResponse createPackaging(PackagingCreateRequest request) {
        Packaging packaging = Packaging.builder()
                .name(request.getName())
                .price(request.getPrice())
                .isAvailable(false)
                .build();

        return PackagingResponse.from(packagingRepository.save(packaging));
    }


    // 조회
    public PackagingResponse getPackaging(long packagingId) {
        Packaging packaging = getPackagingOrThrow(packagingId);
        return PackagingResponse.from(packaging);
    }

    // 활성화된 포장정책 list 조회
    public PageResponse<PackagingResponse> getAvailablePackagingList(Pageable pageable) {
        Page<Packaging> page = packagingRepository.findByIsAvailableTrue(pageable);
        Page<PackagingResponse> packagingResponseList = page.map(PackagingResponse::from);
        return PageResponse.from(packagingResponseList);
    }

    // 전체 포장정책 list 조회
    public PageResponse<PackagingResponse> getPackagingList(Pageable pageable) {
        Page<Packaging> page = packagingRepository.findAll(pageable);
        Page<PackagingResponse> packagingResponseList = page.map(PackagingResponse::from);
        return PageResponse.from(packagingResponseList);
    }

    // 수정
    @Transactional
    public PackagingResponse updatePackaging(long packagingId, PackagingUpdateRequest request) {
        Packaging packaging = getPackagingOrThrow(packagingId);
        packaging.update(request);
        return PackagingResponse.from(packagingRepository.save(packaging));
    }

    // 삭제
    @Transactional
    public void deletePackaging(long packagingId) {
        getPackagingOrThrow(packagingId);
        packagingRepository.deleteById(packagingId);
    }


    // 활성화
    @Transactional
    public void activate(long packagingId) {
        Packaging packaging = getPackagingOrThrow(packagingId);
        packaging.activate();
        packagingRepository.save(packaging);
    }

    // 비활성화
    @Transactional
    public void deactivate(long packagingId) {
        Packaging packaging = getPackagingOrThrow(packagingId);
        packaging.deactivate();
        packagingRepository.save(packaging);
    }


    // 조회 로직
    private Packaging getPackagingOrThrow(long packagingId) {
        Optional<Packaging> optionalPackaging = packagingRepository.findById(packagingId);
        if (!optionalPackaging.isPresent()) {
            throw new PackagingNotFoundException();
        }
        return optionalPackaging.get();
    }
}
