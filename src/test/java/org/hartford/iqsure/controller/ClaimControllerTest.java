package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.response.ClaimResponseDTO;
import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimControllerTest {

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimController claimController;

    private Claim createMockClaim() {
        User user = User.builder().userId(1L).name("Test User").build();
        return Claim.builder()
                .id(1L)
                .claimNumber("CLM-12345")
                .user(user)
                .status(Claim.ClaimStatus.SUBMITTED)
                .build();
    }

    @Test
    public void testGetAllClaims() {
        // --- ARRANGE ---
        when(claimService.getAllClaims()).thenReturn(List.of(createMockClaim()));

        // --- ACT ---
        ResponseEntity<List<ClaimResponseDTO>> response = claimController.getAllClaims();

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(claimService).getAllClaims();
    }

    @Test
    public void testFileClaim() {
        // --- ARRANGE ---
        Claim claim = createMockClaim();
        when(claimService.fileClaim(anyLong(), anyLong(), any(Claim.class))).thenReturn(claim);

        // --- ACT ---
        ResponseEntity<ClaimResponseDTO> response = claimController.fileClaim(1L, 1L, claim);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals("CLM-12345", response.getBody().getClaimNumber());
    }

    @Test
    public void testAssignOfficer() {
        // --- ARRANGE ---
        Claim claim = createMockClaim();
        when(claimService.assignOfficer(1L, 2L)).thenReturn(claim);

        // --- ACT ---
        ResponseEntity<ClaimResponseDTO> response = claimController.assignOfficer(1L, 2L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        verify(claimService).assignOfficer(1L, 2L);
    }
}
