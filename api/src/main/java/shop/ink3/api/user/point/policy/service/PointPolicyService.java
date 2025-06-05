package shop.ink3.api.user.point.policy.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.point.policy.dto.PointPolicyCreateRequest;
import shop.ink3.api.user.point.policy.dto.PointPolicyResponse;
import shop.ink3.api.user.point.policy.dto.PointPolicyStatisticsResponse;
import shop.ink3.api.user.point.policy.dto.PointPolicyUpdateRequest;
import shop.ink3.api.user.point.policy.entity.PointPolicy;
import shop.ink3.api.user.point.policy.exception.CannotDeleteActivePointPolicyException;
import shop.ink3.api.user.point.policy.exception.PointPolicyNotFoundException;
import shop.ink3.api.user.point.policy.repository.PointPolicyRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class PointPolicyService {
    private final PointPolicyRepository pointPolicyRepository;

    @Transactional(readOnly = true)
    public PointPolicyResponse getPointPolicy(long pointPolicyId) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new PointPolicyNotFoundException(pointPolicyId));
        return PointPolicyResponse.from(pointPolicy);
    }

    @Transactional(readOnly = true)
    public PageResponse<PointPolicyResponse> getPointPolicies(Pageable pageable) {
        return PageResponse.from(pointPolicyRepository.findAll(pageable).map(PointPolicyResponse::from));
    }

    @Transactional(readOnly = true)
    public PointPolicyStatisticsResponse getPointPolicyStatistics() {
        return pointPolicyRepository.getPointPolicyStatistics();
    }

    public PointPolicyResponse createPointPolicy(PointPolicyCreateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.save(new PointPolicy(
                request.name(),
                request.joinPoint(),
                request.reviewPoint(),
                request.defaultRate()
        ));
        return PointPolicyResponse.from(pointPolicy);
    }

    public PointPolicyResponse updatePointPolicy(long pointPolicyId, PointPolicyUpdateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new PointPolicyNotFoundException(pointPolicyId));
        pointPolicy.update(request.name(), request.joinPoint(), request.reviewPoint(), request.defaultRate());
        pointPolicyRepository.save(pointPolicy);
        return PointPolicyResponse.from(pointPolicy);
    }

    public PointPolicyResponse activatePointPolicy(long pointPolicyId) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new PointPolicyNotFoundException(pointPolicyId));
        pointPolicyRepository.findByIsActive(true).ifPresent(currentActivePolicy -> {
            if (!Objects.equals(pointPolicy.getId(), currentActivePolicy.getId())) {
                currentActivePolicy.deactivate();
                pointPolicyRepository.save(currentActivePolicy);
            }
        });

        if (!pointPolicy.getIsActive()) {
            pointPolicy.activate();
            pointPolicyRepository.save(pointPolicy);
        }

        return PointPolicyResponse.from(pointPolicy);
    }

    public void deletePointPolicy(long pointPolicyId) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(pointPolicyId)
                .orElseThrow(() -> new PointPolicyNotFoundException(pointPolicyId));
        if (pointPolicy.getIsActive()) {
            throw new CannotDeleteActivePointPolicyException();
        }
        pointPolicyRepository.deleteById(pointPolicyId);
    }
}
