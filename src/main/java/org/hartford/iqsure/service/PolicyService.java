/*
 * FILE: PolicyService.java | LOCATION: service/
 * PURPOSE: Insurance policy CRUD logic. Admin creates/updates/deletes policies.
 * CALLED BY: PolicyController.java
 * USES: PolicyRepository
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.PolicyRequestDTO;
import org.hartford.iqsure.dto.response.PolicyResponseDTO;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    @Transactional
    public PolicyResponseDTO createPolicy(PolicyRequestDTO dto) {
        if (policyRepository.existsByTitle(dto.getTitle())) {
            throw new BadRequestException("A policy with this title already exists: " + dto.getTitle());
        }

        Policy policy = Policy.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .policyType(dto.getPolicyType())
                .basePremium(dto.getBasePremium())
                .coverageAmount(dto.getCoverageAmount())
                .durationMonths(dto.getDurationMonths())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .ageRange(dto.getAgeRange())
                .planType(dto.getPlanType())
                .waitingPeriod(dto.getWaitingPeriod())
                .hasMaternityCover(dto.getHasMaternityCover())
                .hasPreExistingCover(dto.getHasPreExistingCover())
                .build();

        return toDTO(policyRepository.save(policy));
    }

    public PolicyResponseDTO getPolicyById(Long policyId) {
        return toDTO(findOrThrow(policyId));
    }

    public List<PolicyResponseDTO> getAllPolicies() {
        return policyRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<PolicyResponseDTO> getActivePolicies() {
        return policyRepository.findByIsActiveTrue().stream().map(this::toDTO).toList();
    }

    @Transactional
    public PolicyResponseDTO updatePolicy(Long policyId, PolicyRequestDTO dto) {
        Policy policy = findOrThrow(policyId);

        if (!policy.getTitle().equals(dto.getTitle()) && policyRepository.existsByTitle(dto.getTitle())) {
            throw new BadRequestException("A policy with this title already exists: " + dto.getTitle());
        }

        policy.setTitle(dto.getTitle());
        policy.setDescription(dto.getDescription());
        policy.setPolicyType(dto.getPolicyType());
        policy.setBasePremium(dto.getBasePremium());
        policy.setCoverageAmount(dto.getCoverageAmount());
        policy.setDurationMonths(dto.getDurationMonths());
        if (dto.getIsActive() != null) {
            policy.setIsActive(dto.getIsActive());
        }
        policy.setAgeRange(dto.getAgeRange());
        policy.setPlanType(dto.getPlanType());
        policy.setWaitingPeriod(dto.getWaitingPeriod());
        policy.setHasMaternityCover(dto.getHasMaternityCover());
        policy.setHasPreExistingCover(dto.getHasPreExistingCover());

        return toDTO(policyRepository.save(policy));
    }

    @Transactional
    public void deletePolicy(Long policyId) {
        if (!policyRepository.existsById(policyId)) {
            throw new ResourceNotFoundException("Policy not found with id: " + policyId);
        }
        policyRepository.deleteById(policyId);
    }

    private Policy findOrThrow(Long policyId) {
        return policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + policyId));
    }

    private PolicyResponseDTO toDTO(Policy p) {
        return PolicyResponseDTO.builder()
                .policyId(p.getPolicyId())
                .title(p.getTitle())
                .description(p.getDescription())
                .policyType(p.getPolicyType())
                .basePremium(p.getBasePremium())
                .coverageAmount(p.getCoverageAmount())
                .durationMonths(p.getDurationMonths())
                .isActive(p.getIsActive())
                .ageRange(p.getAgeRange())
                .planType(p.getPlanType())
                .waitingPeriod(p.getWaitingPeriod())
                .hasMaternityCover(p.getHasMaternityCover())
                .hasPreExistingCover(p.getHasPreExistingCover())
                .build();
    }
}
