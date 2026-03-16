package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.request.UserPolicyRequestDTO;
import org.hartford.iqsure.dto.response.PremiumBreakdownDTO;
import org.hartford.iqsure.dto.response.UserPolicyResponseDTO;
import org.hartford.iqsure.service.PremiumCalculationService;
import org.hartford.iqsure.service.UserPolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPolicyControllerTest {

    @Mock
    private UserPolicyService userPolicyService;

    @Mock
    private PremiumCalculationService premiumCalculationService;

    @InjectMocks
    private UserPolicyController userPolicyController;

    @Test
    public void testGetUserPolicies() {
        // --- ARRANGE ---
        UserPolicyResponseDTO dto = UserPolicyResponseDTO.builder().id(1L).build();
        when(userPolicyService.getUserPolicies(1L)).thenReturn(List.of(dto));

        // --- ACT ---
        ResponseEntity<List<UserPolicyResponseDTO>> response = userPolicyController.getUserPolicies(1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        verify(userPolicyService).getUserPolicies(1L);
    }

    @Test
    public void testCalculatePremium() {
        // --- ARRANGE ---
        PremiumBreakdownDTO breakdown = PremiumBreakdownDTO.builder().finalPremium(500.0).build();
        when(premiumCalculationService.calculatePremium(anyLong(), anyLong(), any())).thenReturn(breakdown);

        // --- ACT ---
        ResponseEntity<PremiumBreakdownDTO> response = userPolicyController.calculatePremium(1L, 1L, Collections.emptyList());

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(500.0, response.getBody().getFinalPremium());
    }

    @Test
    public void testPurchasePolicy() {
        // --- ARRANGE ---
        UserPolicyRequestDTO request = new UserPolicyRequestDTO();
        UserPolicyResponseDTO responseDTO = UserPolicyResponseDTO.builder().id(1L).build();
        when(userPolicyService.purchasePolicy(anyLong(), any(UserPolicyRequestDTO.class), any())).thenReturn(responseDTO);

        // --- ACT ---
        ResponseEntity<UserPolicyResponseDTO> response = userPolicyController.purchasePolicy(1L, request, null);

        // --- ASSERT ---
        assertEquals(201, response.getStatusCode().value());
        verify(userPolicyService).purchasePolicy(eq(1L), eq(request), any());
    }
}
