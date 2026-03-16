package org.hartford.iqsure.service;

import org.hartford.iqsure.dto.request.UserPolicyRequestDTO;
import org.hartford.iqsure.dto.response.PremiumBreakdownDTO;
import org.hartford.iqsure.dto.response.UserPolicyResponseDTO;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.PolicyRepository;
import org.hartford.iqsure.repository.UserPolicyRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPolicyServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private UserPolicyRepository userPolicyRepository;
    @Mock
    private PremiumCalculationService premiumCalculationService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserPolicyService userPolicyService;

    @Test
    public void testPurchasePolicy_Successful() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).name("John").build();
        Policy policy = Policy.builder().policyId(1L).title("Health").isActive(true).basePremium(1000.0).coverageAmount(500000L).build();
        UserPolicyRequestDTO dto = new UserPolicyRequestDTO();
        dto.setPolicyId(1L);

        PremiumBreakdownDTO breakdown = PremiumBreakdownDTO.builder()
                .finalPremium(900.0)
                .totalDiscountPercent(10.0)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(premiumCalculationService.calculatePremium(eq(1L), eq(1L), any())).thenReturn(breakdown);
        when(userPolicyRepository.save(any(UserPolicy.class))).thenAnswer(i -> {
            UserPolicy up = (UserPolicy) i.getArguments()[0];
            up.setId(1L);
            return up;
        });

        // --- ACT ---
        UserPolicyResponseDTO result = userPolicyService.purchasePolicy(1L, dto, Collections.emptyList());

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(900.0, result.getFinalPremium());
        verify(notificationService).createNotificationForAdmins(anyString(), any(), any(), anyString());
    }

    @Test
    public void testAssignUnderwriter_Successful() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).build();
        Policy policy = Policy.builder().title("P1").basePremium(100.0).build();
        UserPolicy up = UserPolicy.builder().id(1L).user(user).policy(policy).build();
        User underwriter = User.builder().userId(2L).role(User.Role.ROLE_UNDERWRITER).build();

        when(userPolicyRepository.findById(1L)).thenReturn(Optional.of(up));
        when(userRepository.findById(2L)).thenReturn(Optional.of(underwriter));
        when(userPolicyRepository.save(any(UserPolicy.class))).thenReturn(up);

        // --- ACT ---
        UserPolicyResponseDTO result = userPolicyService.assignUnderwriter(1L, 2L);

        // --- ASSERT ---
        assertEquals(UserPolicy.PolicyStatus.UNDER_EVALUATION, result.getStatus());
        verify(notificationService, times(2)).createNotification(anyLong(), anyString(), any(), any(), anyString());
    }
}
