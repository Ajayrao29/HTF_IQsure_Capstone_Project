/*
 * FILE: PolicyRequestDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO for creating/updating an insurance policy. Admin sends this.
 * FLOW: PolicyMgmtComponent → api.service.ts → POST/PUT /api/v1/policies → PolicyController → PolicyService
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hartford.iqsure.entity.Policy;

/**
 * Request body for creating or updating a Policy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Policy type is required (LIFE, HEALTH, MOTOR)")
    private Policy.PolicyType policyType;

    @NotNull(message = "Base premium is required")
    @Positive(message = "Base premium must be positive")
    private Double basePremium;

    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    private Double coverageAmount;

    @NotNull(message = "Duration in months is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    private Integer durationMonths;

    @Builder.Default
    private Boolean isActive = true;

    private String ageRange;
    private String planType;

    private String waitingPeriod;
    private Boolean hasMaternityCover;
    private Boolean hasPreExistingCover;
}
