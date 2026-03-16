package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.request.PolicyRequestDTO;
import org.hartford.iqsure.dto.response.PolicyResponseDTO;
import org.hartford.iqsure.service.PolicyService;
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
public class PolicyControllerTest {

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    @Test
    public void testGetActivePolicies() {
        // --- ARRANGE ---
        PolicyResponseDTO dto = PolicyResponseDTO.builder().policyId(1L).title("Active").build();
        when(policyService.getActivePolicies()).thenReturn(List.of(dto));

        // --- ACT ---
        ResponseEntity<List<PolicyResponseDTO>> response = policyController.getActivePolicies();

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(policyService).getActivePolicies();
    }

    @Test
    public void testCreate() {
        // --- ARRANGE ---
        PolicyRequestDTO request = new PolicyRequestDTO();
        request.setTitle("New Policy");
        PolicyResponseDTO responseDTO = PolicyResponseDTO.builder().policyId(1L).title("New Policy").build();
        when(policyService.createPolicy(any(PolicyRequestDTO.class))).thenReturn(responseDTO);

        // --- ACT ---
        ResponseEntity<PolicyResponseDTO> response = policyController.create(request);

        // --- ASSERT ---
        assertEquals(201, response.getStatusCode().value());
        assertEquals("New Policy", response.getBody().getTitle());
        verify(policyService).createPolicy(any(PolicyRequestDTO.class));
    }

    @Test
    public void testDelete() {
        // --- ACT ---
        ResponseEntity<Void> response = policyController.delete(1L);

        // --- ASSERT ---
        assertEquals(204, response.getStatusCode().value());
        verify(policyService).deletePolicy(1L);
    }
}
