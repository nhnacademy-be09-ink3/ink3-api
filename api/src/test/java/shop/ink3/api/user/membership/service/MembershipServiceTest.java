package shop.ink3.api.user.membership.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.membership.dto.MembershipCreateRequest;
import shop.ink3.api.user.membership.dto.MembershipResponse;
import shop.ink3.api.user.membership.dto.MembershipUpdateRequest;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.membership.exception.CannotDeactivateDefaultMembershipException;
import shop.ink3.api.user.membership.exception.CannotDeleteDefaultMembershipException;
import shop.ink3.api.user.membership.exception.DefaultMembershipNotFoundException;
import shop.ink3.api.user.membership.exception.MembershipNotFoundException;
import shop.ink3.api.user.membership.repository.MembershipRepository;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {
    @Mock
    MembershipRepository membershipRepository;

    @InjectMocks
    MembershipService membershipService;

    @Test
    void getMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                true,
                LocalDateTime.now()
        );
        when(membershipRepository.findById(anyLong())).thenReturn(Optional.of(membership));
        MembershipResponse response = membershipService.getMembership(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(MembershipResponse.from(membership), response);
    }

    @Test
    void getMembershipWithNotFound() {
        when(membershipRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(MembershipNotFoundException.class, () -> membershipService.getMembership(1L));
    }

    @Test
    void getMemberships() {
        List<Membership> membershipList = List.of(
                new Membership(
                        1L,
                        "test1",
                        1,
                        1,
                        true,
                        true,
                        LocalDateTime.now()
                ),
                new Membership(
                        2L,
                        "test2",
                        1,
                        1,
                        true,
                        false,
                        LocalDateTime.now()
                )
        );
        Pageable pageable = PageRequest.of(0, 2);
        Page<Membership> page = new PageImpl<>(membershipList, pageable, membershipList.size());

        when(membershipRepository.findAll(pageable)).thenReturn(page);

        PageResponse<MembershipResponse> response = membershipService.getMemberships(pageable);

        Assertions.assertEquals(2, response.content().size());
        Assertions.assertEquals(0, response.page());
        Assertions.assertEquals(2, response.size());
        Assertions.assertEquals(1, response.totalPages());
        Assertions.assertFalse(response.hasNext());
    }

    @Test
    void getDefaultMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                true,
                LocalDateTime.now()
        );
        when(membershipRepository.findByIsDefault(true)).thenReturn(Optional.of(membership));
        MembershipResponse response = membershipService.getDefaultMembership();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(MembershipResponse.from(membership), response);
    }

    @Test
    void getDefaultMembershipWithNotFound() {
        when(membershipRepository.findByIsDefault(true)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                DefaultMembershipNotFoundException.class,
                () -> membershipService.getDefaultMembership()
        );
    }

    @Test
    void createMembershipWithDefaultMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                false,
                false,
                LocalDateTime.now()
        );
        when(membershipRepository.existsByIsDefault(true)).thenReturn(true);
        when(membershipRepository.save(any())).thenReturn(membership);
        MembershipResponse membershipResponse = membershipService.createMembership(
                new MembershipCreateRequest(
                        "test",
                        1,
                        1
                )
        );
        Assertions.assertNotNull(membershipResponse);
        Assertions.assertEquals(MembershipResponse.from(membership), membershipResponse);
    }

    @Test
    void createMembershipWithoutDefaultMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                false,
                false,
                LocalDateTime.now()
        );
        when(membershipRepository.existsByIsDefault(true)).thenReturn(false);
        when(membershipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        MembershipResponse membershipResponse = membershipService.createMembership(
                new MembershipCreateRequest(
                        "test",
                        1,
                        1
                )
        );
        Assertions.assertNotNull(membershipResponse);
        Assertions.assertEquals(membership.getName(), membershipResponse.name());
        Assertions.assertEquals(membership.getConditionAmount(), membershipResponse.conditionAmount());
        Assertions.assertEquals(membership.getPointRate(), membershipResponse.pointRate());
        Assertions.assertEquals(true, membershipResponse.isActive());
        Assertions.assertEquals(true, membershipResponse.isDefault());
    }

    @Test
    void updateMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                true,
                LocalDateTime.now()
        );

        MembershipUpdateRequest request = new MembershipUpdateRequest(
                "newName",
                2,
                2
        );

        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));
        when(membershipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MembershipResponse response = membershipService.updateMembership(1L, request);

        Assertions.assertEquals("newName", response.name());
        Assertions.assertEquals(2, response.conditionAmount());
        Assertions.assertEquals(2, response.pointRate());
    }

    @Test
    void updateMembershipWithNotFound() {
        when(membershipRepository.findById(1L)).thenReturn(Optional.empty());

        MembershipUpdateRequest request = new MembershipUpdateRequest(
                "newName",
                2,
                2
        );

        Assertions.assertThrows(
                MembershipNotFoundException.class,
                () -> membershipService.updateMembership(1L, request)
        );
    }

    @Test
    void activateMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                false,
                false,
                LocalDateTime.now()
        );

        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));

        membershipService.activateMembership(1L);

        Assertions.assertTrue(membership.getIsActive());
    }

    @Test
    void activateMembershipWithNotFound() {
        when(membershipRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(MembershipNotFoundException.class, () -> membershipService.activateMembership(1L));
    }

    @Test
    void deactivateMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                false,
                LocalDateTime.now()
        );

        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));

        membershipService.deactivateMembership(1L);

        Assertions.assertFalse(membership.getIsActive());
    }

    @Test
    void deactivateDefaultMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                true,
                LocalDateTime.now()
        );

        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));

        Assertions.assertThrows(CannotDeactivateDefaultMembershipException.class,
                () -> membershipService.deactivateMembership(1L));
    }

    @Test
    void deactivateMembershipWithNotFound() {
        when(membershipRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(MembershipNotFoundException.class, () -> membershipService.deactivateMembership(1L));
    }

    @Test
    void setDefaultMembershipWithDefaultMembership() {
        Membership defaultMembership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                true,
                LocalDateTime.now()
        );
        Membership membership = new Membership(
                2L,
                "test",
                1,
                1,
                true,
                false,
                LocalDateTime.now()
        );
        when(membershipRepository.findById(2L)).thenReturn(Optional.of(membership));
        when(membershipRepository.findByIsDefault(true)).thenReturn(Optional.of(defaultMembership));
        membershipService.setDefaultMembership(2L);
        Assertions.assertFalse(defaultMembership.getIsDefault());
        Assertions.assertTrue(membership.getIsDefault());
    }

    @Test
    void setDefaultMembershipWithoutDefaultMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                false,
                LocalDateTime.now()
        );
        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));
        when(membershipRepository.findByIsDefault(true)).thenReturn(Optional.empty());
        membershipService.setDefaultMembership(1L);
        Assertions.assertTrue(membership.getIsDefault());
    }

    @Test
    void setDefaultMembershipWithDefaultMembershipEqualTargetMembership() {
        Membership membership = new Membership(
                1L,
                "test",
                1,
                1,
                true,
                true,
                LocalDateTime.now()
        );

        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));
        when(membershipRepository.findByIsDefault(true)).thenReturn(Optional.of(membership));

        membershipService.setDefaultMembership(1L);

        Assertions.assertTrue(membership.getIsDefault());
    }

    @Test
    void setDefaultMembershipWithNotFound() {
        when(membershipRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(MembershipNotFoundException.class, () -> membershipService.setDefaultMembership(1L));
    }

    @Test
    void deleteMembership() {
        Membership membership = Membership.builder().id(1L).build();
        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));
        membershipService.deleteMembership(1L);
        verify(membershipRepository).delete(membership);
    }

    @Test
    void deleteDefaultMembership() {
        Membership membership = Membership.builder().id(1L).isDefault(true).build();
        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));
        Assertions.assertThrows(CannotDeleteDefaultMembershipException.class,
                () -> membershipService.deleteMembership(1L));
    }

    @Test
    void deleteMembershipWithNotFound() {
        when(membershipRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(MembershipNotFoundException.class, () -> membershipService.deleteMembership(1L));
    }
}
