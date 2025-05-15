package shop.ink3.api.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.point.exception.PointPolicyNotFoundException;
import shop.ink3.api.point.PointPolicy;
import shop.ink3.api.point.repository.PointPolicyRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    @Override
    @Transactional
    public PointPolicy createPointPolicy(String name, Integer earnPoint, Boolean isAvailable) {
        PointPolicy pointPolicy = PointPolicy.builder()
                .name(name)
                .earnPoint(earnPoint)
                .createdAt(LocalDateTime.now())
                .isAvailable(isAvailable != null ? isAvailable : true)
                .build();

        return pointPolicyRepository.save(pointPolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public PointPolicy getPointPolicyById(Long id) {
        return pointPolicyRepository.findById(id)
                .orElseThrow(() -> new PointPolicyNotFoundException("해당 ID의 포인트 정책을 찾을 수 없습니다: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PointPolicy getPointPolicyByName(String name) {
        PointPolicy pointPolicy = pointPolicyRepository.findByName(name);
        if (pointPolicy == null) {
            throw new PointPolicyNotFoundException("해당 이름의 포인트 정책을 찾을 수 없습니다: " + name);
        }
        return pointPolicy;
    }

    @Override
    @Transactional(readOnly = true)
    public PointPolicy getActivePointPolicy() {
        PointPolicy pointPolicy = pointPolicyRepository.findByIsAvailableTrue();
        if (pointPolicy == null) {
            throw new PointPolicyNotFoundException("활성화된 포인트 정책이 없습니다.");
        }
        return pointPolicy;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointPolicy> getAllPointPolicies() {
        return pointPolicyRepository.findAll();
    }

    @Override
    @Transactional
    public PointPolicy updatePointPolicy(Long id, String name, Integer earnPoint, Boolean isAvailable) {
        PointPolicy pointPolicy = getPointPolicyById(id);
        pointPolicy.update(name, earnPoint, isAvailable);
        return pointPolicyRepository.save(pointPolicy);
    }

    @Override
    @Transactional
    public PointPolicy activatePointPolicy(Long id) {
        PointPolicy pointPolicy = getPointPolicyById(id).activate();
        return pointPolicyRepository.save(pointPolicy);
    }

    @Override
    @Transactional
    public PointPolicy deactivatePointPolicy(Long id) {
        PointPolicy pointPolicy = getPointPolicyById(id).deactivate();
        return pointPolicyRepository.save(pointPolicy);
    }

    @Override
    @Transactional
    public void deletePointPolicy(Long id) {
        pointPolicyRepository.deleteById(id);
    }
}
