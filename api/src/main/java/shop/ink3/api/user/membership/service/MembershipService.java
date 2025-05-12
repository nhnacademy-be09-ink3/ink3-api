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
import shop.ink3.api.user.membership.exception.DefaultMembershipNotFoundException;
import shop.ink3.api.user.membership.exception.MembershipNotFoundException;
import shop.ink3.api.user.membership.repository.MembershipRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public MembershipResponse getMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        return MembershipResponse.from(membership);
    }

    @Transactional(readOnly = true)
    public PageResponse<MembershipResponse> getMemberships(Pageable pageable) {
        Page<Membership> page = membershipRepository.findAll(pageable);
        return PageResponse.from(page.map(MembershipResponse::from));
    }

    @Transactional(readOnly = true)
    public MembershipResponse getDefaultMembership() {
        Membership membership = membershipRepository.findByIsDefault(true)
                .orElseThrow(DefaultMembershipNotFoundException::new);
        return MembershipResponse.from(membership);
    }

    public MembershipResponse createMembership(MembershipCreateRequest request) {
        boolean hasDefault = membershipRepository.existsByIsDefault(true);
        Membership membership = Membership.builder()
                .name(request.name())
                .conditionAmount(request.conditionAmount())
                .pointRate(request.pointRate())
                .isActive(!hasDefault)
                .isDefault(!hasDefault)
                .createdAt(LocalDateTime.now())
                .build();
        return MembershipResponse.from(membershipRepository.save(membership));
    }

    public MembershipResponse updateMembership(long membershipId, MembershipUpdateRequest request) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        membership.update(request.name(), request.conditionAmount(), request.pointRate());
        return MembershipResponse.from(membershipRepository.save(membership));
    }

    public void activateMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        membership.activate();
        membershipRepository.save(membership);
    }

    public void deactivateMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        if (membership.getIsDefault()) {
            throw new IllegalStateException("Default membership cannot be deactivated.");
        }
        membership.deactivate();
        membershipRepository.save(membership);
    }

    public void setDefaultMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        membershipRepository.findByIsDefault(true).ifPresent(currentDefaultMembership -> {
            if (!currentDefaultMembership.getId().equals(membership.getId())) {
                currentDefaultMembership.unmarkAsDefault();
                membershipRepository.save(currentDefaultMembership);
            }
        });
        membership.markAsDefault();
        membershipRepository.save(membership);
    }

    public void deleteMembership(long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipNotFoundException(membershipId));
        if (membership.getIsDefault()) {
            throw new IllegalStateException("Default membership cannot be deleted.");
        }
        membershipRepository.delete(membership);
    }
}
