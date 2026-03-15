/*
 * FILE: UserPolicyResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO for a user's purchased policy. Shows policy details + discount info.
 *          Maps to "UserPolicy" interface in models/models.ts.
 * RETURNED BY: UserPolicyController endpoints → UserPolicyService.toDTO()
 * USED IN FRONTEND: MyPoliciesComponent (pages/my-policies/)
 */
package org.hartford.iqsure.dto.response;

import lombok.*;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.entity.UserPolicy;

import java.time.LocalDateTime;

/**
 * Response DTO for a user's purchased policy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicyResponseDTO {

    private Long id;
    private Long userId;
    private String userName;

    // Policy summary
    private Long policyId;
    private String policyTitle;
    private Policy.PolicyType policyType;
    private Double basePremium;
    private Double coverageAmount;
    private Integer durationMonths;

    // Purchase details
    private Double finalPremium;
    private Double discountApplied;
    private LocalDateTime purchaseDate;
    private UserPolicy.PolicyStatus status;

    /**
     * Amount saved due to gamification discounts.
     */
    private Double savedAmount;

    // Pipeline details
    private Long assignedUnderwriterId;
    private String assignedUnderwriterName;
    private java.time.LocalDateTime assignedAt;
    private java.math.BigDecimal quoteAmount;
    private String underwriterRemarks;
    private java.math.BigDecimal totalClaimedAmount;
    private java.math.BigDecimal remainingCoverage;

    // Application details
    private String nomineeName;
    private String nomineeRelationship;
    private String healthReportPath;
    private java.util.List<InsuredMemberResponseDTO> insuredMembers;
}
