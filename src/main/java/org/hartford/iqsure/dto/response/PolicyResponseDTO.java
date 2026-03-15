/*
 * FILE: PolicyResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO for policy data sent to frontend. Maps to "Policy" in models/models.ts.
 * RETURNED BY: PolicyController endpoints → PolicyService.toDTO()
 * USED IN FRONTEND: PoliciesComponent (pages/policies/), PolicyMgmtComponent (pages/admin/policy-mgmt/)
 */
package org.hartford.iqsure.dto.response;

import lombok.*;
import org.hartford.iqsure.entity.Policy;

/**
 * Response DTO for a Policy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyResponseDTO {

    private Long policyId;
    private String title;
    private String description;
    private Policy.PolicyType policyType;
    private Double basePremium;
    private Double coverageAmount;
    private Integer durationMonths;
    private Boolean isActive;
    private String ageRange;
    private String planType;
    private String waitingPeriod;
    private Boolean hasMaternityCover;
    private Boolean hasPreExistingCover;
}
