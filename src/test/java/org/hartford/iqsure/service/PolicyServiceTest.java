package org.hartford.iqsure.service;

import org.hartford.iqsure.dto.request.PolicyRequestDTO;
import org.hartford.iqsure.dto.response.PolicyResponseDTO;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    @Test
    public void testCreatePolicy_Successful() {
        // --- ARRANGE ---
        PolicyRequestDTO dto = new PolicyRequestDTO();
        dto.setTitle("Gold Plan");
        dto.setBasePremium(new BigDecimal("1000.00"));

        Policy savedPolicy = Policy.builder()
                .policyId(1L)
                .title("Gold Plan")
                .basePremium(new BigDecimal("1000.00"))
                .build();

        when(policyRepository.existsByTitle("Gold Plan")).thenReturn(false);
        when(policyRepository.save(any(Policy.class))).thenReturn(savedPolicy);

        // --- ACT ---
        PolicyResponseDTO result = policyService.createPolicy(dto);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("Gold Plan", result.getTitle());
        verify(policyRepository).save(any(Policy.class));
    }

    @Test
    public void testCreatePolicy_DuplicateTitle_ThrowsException() {
        // --- ARRANGE ---
        PolicyRequestDTO dto = new PolicyRequestDTO();
        dto.setTitle("Duplicate");

        when(policyRepository.existsByTitle("Duplicate")).thenReturn(true);

        // --- ACT & ASSERT ---
        assertThrows(BadRequestException.class, () -> policyService.createPolicy(dto));
    }

    @Test
    public void testGetPolicyById_Successful() {
        // --- ARRANGE ---
        Policy policy = Policy.builder().policyId(1L).title("Plan").build();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // --- ACT ---
        PolicyResponseDTO result = policyService.getPolicyById(1L);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("Plan", result.getTitle());
    }

    @Test
    public void testGetAllPolicies() {
        // --- ARRANGE ---
        Policy p1 = Policy.builder().policyId(1L).build();
        when(policyRepository.findAll()).thenReturn(List.of(p1));

        // --- ACT ---
        List<PolicyResponseDTO> result = policyService.getAllPolicies();

        // --- ASSERT ---
        assertEquals(1, result.size());
    }

    @Test
    public void testDeletePolicy_NotFound_ThrowsException() {
        // --- ARRANGE ---
        when(policyRepository.existsById(99L)).thenReturn(false);

        // --- ACT & ASSERT ---
        assertThrows(ResourceNotFoundException.class, () -> policyService.deletePolicy(99L));
    }
}
