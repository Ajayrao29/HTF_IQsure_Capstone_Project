/*
 * FILE: DiscountRuleRequestDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO for creating/updating discount rules. Admin sends this.
 * FLOW: DiscountRulesComponent → api.service.ts → POST/PUT /api/v1/discount-rules
 *       → DiscountRuleController → DiscountRuleService
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hartford.iqsure.entity.Policy;

/**
 * Request body for creating or updating a DiscountRule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRuleRequestDTO {

    @NotBlank(message = "Rule name is required")
    private String ruleName;

    private String description;

    /**
     * Minimum quiz score percentage required. Default 0 = ignore.
     */
    @DecimalMin(value = "0.0", message = "Min quiz score cannot be negative")
    @DecimalMax(value = "100.0", message = "Min quiz score cannot exceed 100")
    @Builder.Default
    private Double minQuizScorePercent = 0.0;

    /**
     * Minimum user points required. Default 0 = ignore.
     */
    @Min(value = 0, message = "Min user points cannot be negative")
    @Builder.Default
    private Integer minUserPoints = 0;

    /**
     * Minimum badges earned required. Default 0 = ignore.
     */
    @Min(value = 0, message = "Min badges earned cannot be negative")
    @Builder.Default
    private Integer minBadgesEarned = 0;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.1", message = "Discount must be at least 0.1%")
    @DecimalMax(value = "50.0", message = "Single rule discount cannot exceed 50%")
    private Double discountPercentage;

    /**
     * Optional: scope this rule to a specific policy type.
     * Leave null to apply to all policy types.
     */
    private Policy.PolicyType applicablePolicyType;

    @Builder.Default
    private Boolean isActive = true;
}
