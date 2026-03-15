/*
 * FILE: DiscountRuleService.java | LOCATION: service/
 * PURPOSE: Discount rule CRUD logic. Admin creates rules that define how gamification
 *          data (points, badges, quiz scores) translates into premium discounts.
 * CALLED BY: DiscountRuleController.java
 * USES: DiscountRuleRepository
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.DiscountRuleRequestDTO;
import org.hartford.iqsure.dto.response.DiscountRuleResponseDTO;
import org.hartford.iqsure.entity.DiscountRule;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.DiscountRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountRuleService {

    private final DiscountRuleRepository discountRuleRepository;

    @Transactional
    public DiscountRuleResponseDTO createRule(DiscountRuleRequestDTO dto) {
        if (discountRuleRepository.existsByRuleName(dto.getRuleName())) {
            throw new BadRequestException("A discount rule with this name already exists: " + dto.getRuleName());
        }

        DiscountRule rule = DiscountRule.builder()
                .ruleName(dto.getRuleName())
                .description(dto.getDescription())
                .minQuizScorePercent(dto.getMinQuizScorePercent() != null ? dto.getMinQuizScorePercent() : 0.0)
                .minUserPoints(dto.getMinUserPoints() != null ? dto.getMinUserPoints() : 0)
                .minBadgesEarned(dto.getMinBadgesEarned() != null ? dto.getMinBadgesEarned() : 0)
                .discountPercentage(dto.getDiscountPercentage())
                .applicablePolicyType(dto.getApplicablePolicyType())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        return toDTO(discountRuleRepository.save(rule));
    }

    public DiscountRuleResponseDTO getRuleById(Long ruleId) {
        return toDTO(findOrThrow(ruleId));
    }

    public List<DiscountRuleResponseDTO> getAllRules() {
        return discountRuleRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<DiscountRuleResponseDTO> getActiveRules() {
        return discountRuleRepository.findByIsActiveTrue().stream().map(this::toDTO).toList();
    }

    @Transactional
    public DiscountRuleResponseDTO updateRule(Long ruleId, DiscountRuleRequestDTO dto) {
        DiscountRule rule = findOrThrow(ruleId);

        if (!rule.getRuleName().equals(dto.getRuleName()) && discountRuleRepository.existsByRuleName(dto.getRuleName())) {
            throw new BadRequestException("A discount rule with this name already exists: " + dto.getRuleName());
        }

        rule.setRuleName(dto.getRuleName());
        rule.setDescription(dto.getDescription());
        rule.setMinQuizScorePercent(dto.getMinQuizScorePercent() != null ? dto.getMinQuizScorePercent() : 0.0);
        rule.setMinUserPoints(dto.getMinUserPoints() != null ? dto.getMinUserPoints() : 0);
        rule.setMinBadgesEarned(dto.getMinBadgesEarned() != null ? dto.getMinBadgesEarned() : 0);
        rule.setDiscountPercentage(dto.getDiscountPercentage());
        rule.setApplicablePolicyType(dto.getApplicablePolicyType());
        if (dto.getIsActive() != null) {
            rule.setIsActive(dto.getIsActive());
        }

        return toDTO(discountRuleRepository.save(rule));
    }

    @Transactional
    public void deleteRule(Long ruleId) {
        if (!discountRuleRepository.existsById(ruleId)) {
            throw new ResourceNotFoundException("Discount rule not found with id: " + ruleId);
        }
        discountRuleRepository.deleteById(ruleId);
    }

    private DiscountRule findOrThrow(Long ruleId) {
        return discountRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount rule not found with id: " + ruleId));
    }

    private DiscountRuleResponseDTO toDTO(DiscountRule r) {
        return DiscountRuleResponseDTO.builder()
                .ruleId(r.getRuleId())
                .ruleName(r.getRuleName())
                .description(r.getDescription())
                .minQuizScorePercent(r.getMinQuizScorePercent())
                .minUserPoints(r.getMinUserPoints())
                .minBadgesEarned(r.getMinBadgesEarned())
                .discountPercentage(r.getDiscountPercentage())
                .applicablePolicyType(r.getApplicablePolicyType())
                .isActive(r.getIsActive())
                .build();
    }
}
