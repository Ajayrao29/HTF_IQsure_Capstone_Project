package org.hartford.iqsure.service;

import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.repository.ClaimRepository;
import org.hartford.iqsure.repository.UserPolicyRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private UserPolicyRepository userPolicyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ClaimService claimService;

    @Test
    public void testFileClaim_Successful() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).name("John").build();
        Policy policy = Policy.builder().title("Health").build();
        UserPolicy up = UserPolicy.builder().id(1L).policy(policy).build();
        Claim request = Claim.builder().amount(new BigDecimal("500.00")).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPolicyRepository.findById(1L)).thenReturn(Optional.of(up));
        when(claimRepository.save(any(Claim.class))).thenReturn(request);

        // --- ACT ---
        Claim result = claimService.fileClaim(1L, 1L, request);

        // --- ASSERT ---
        assertNotNull(result);
        verify(claimRepository).save(any(Claim.class));
        verify(notificationService).createNotificationForAdmins(anyString(), any(), any(), anyString());
    }

    @Test
    public void testAssignOfficer_Successful() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).build();
        User officer = User.builder().userId(2L).role(User.Role.ROLE_CLAIMS_OFFICER).build();
        Claim claim = Claim.builder().id(1L).user(user).claimNumber("CLM-123").build();

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(userRepository.findById(2L)).thenReturn(Optional.of(officer));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        // --- ACT ---
        Claim result = claimService.assignOfficer(1L, 2L);

        // --- ASSERT ---
        assertEquals(Claim.ClaimStatus.UNDER_REVIEW, result.getStatus());
        assertEquals(officer, result.getAssignedOfficer());
        verify(notificationService, times(2)).createNotification(anyLong(), anyString(), any(), any(), anyString());
    }

    @Test
    public void testProcessClaim_Approved() {
        // --- ARRANGE ---
        User officer = User.builder().userId(2L).build();
        User user = User.builder().userId(1L).build();
        Claim claim = Claim.builder().id(1L).user(user).assignedOfficer(officer).claimNumber("CLM-123").build();

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        // --- ACT ---
        Claim result = claimService.processClaim(1L, Claim.ClaimStatus.APPROVED, "Approved", new BigDecimal("400.00"));

        // --- ASSERT ---
        assertEquals(Claim.ClaimStatus.APPROVED, result.getStatus());
        assertEquals(new BigDecimal("400.00"), result.getApprovedAmount());
        verify(userRepository).save(officer);
    }
}
