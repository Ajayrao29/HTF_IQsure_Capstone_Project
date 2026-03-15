/*
 * FILE: DiscountRuleResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO for discount rule data sent to frontend. Maps to "DiscountRule" in models/models.ts.
 * RETURNED BY: DiscountRuleController endpoints → DiscountRuleService.toDTO()
 * USED IN FRONTEND: DiscountRulesComponent (pages/admin/discount-rules/)
 */
package org.hartford.iqsure.dto.response;

import lombok.*;
import org.hartford.iqsure.entity.Policy;

/**
 * Response DTO for a DiscountRule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRuleResponseDTO {

    private Long ruleId;
    private String ruleName;
    private String description;
    private Double minQuizScorePercent;
    private Integer minUserPoints;
    private Integer minBadgesEarned;
    private Double discountPercentage;
    private Policy.PolicyType applicablePolicyType;
    private Boolean isActive;
}
