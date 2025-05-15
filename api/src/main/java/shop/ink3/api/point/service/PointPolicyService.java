package shop.ink3.api.point.service;

import shop.ink3.api.point.PointPolicy;

import java.util.List;

public interface PointPolicyService {


    PointPolicy createPointPolicy(String name, Integer earnPoint, Boolean isAvailable);


    PointPolicy getPointPolicyById(Long id);

    PointPolicy getPointPolicyByName(String name);


    PointPolicy getActivePointPolicy();

    List<PointPolicy> getAllPointPolicies();


    PointPolicy updatePointPolicy(Long id, String name, Integer earnPoint, Boolean isAvailable);


    PointPolicy activatePointPolicy(Long id);


    PointPolicy deactivatePointPolicy(Long id);


    void deletePointPolicy(Long id);
}
