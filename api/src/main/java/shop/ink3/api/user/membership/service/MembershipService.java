package shop.ink3.api.user.membership.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.membership.dto.MembershipCreateRequest;
import shop.ink3.api.user.membership.dto.MembershipResponse;
import shop.ink3.api.user.membership.dto.MembershipUpdateRequest;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.membership.exception.MembershipNotFoundException;
import shop.ink3.api.user.membership.repository.MembershipRepository;

@RequiredArgsConstructor
@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;

    public MembershipResponse getMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        return MembershipResponse.from(membership);
    }

    public PageResponse<MembershipResponse> getMemberships(Pageable pageable) {
        Page<Membership> page = membershipRepository.findAll(pageable);
        return PageResponse.from(page.map(MembershipResponse::from));
    }

    @Transactional
    public MembershipResponse createMembership(MembershipCreateRequest request) {
        Membership membership = Membership.builder()
                .name(request.name())
                .conditionAmount(request.conditionAmount())
                .pointRate(request.pointRate())
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .build();
        return MembershipResponse.from(membershipRepository.save(membership));
    }

    @Transactional
    public MembershipResponse updateMembership(long membershipId, MembershipUpdateRequest request) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        membership.update(request.name(), request.conditionAmount(), request.pointRate());
        return MembershipResponse.from(membershipRepository.save(membership));
    }

    @Transactional
    public void activateMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        membership.activate();
        membershipRepository.save(membership);
    }

    @Transactional
    public void deactivateMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        membership.deactivate();
        membershipRepository.save(membership);
    }

    @Transactional
    public void deleteMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        membershipRepository.delete(membership);
    }
}
