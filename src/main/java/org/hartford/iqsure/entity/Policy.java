/*
 * ============================================================================
 * FILE: Policy.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "policies" table. An insurance policy template that
 *          users can purchase. Admin creates policies with a base premium price,
 *          and when a user buys it, gamification discounts are applied.
 *
 * DATABASE TABLE: policies
 *   - policyId, title, description, policyType (LIFE/HEALTH/MOTOR),
 *     basePremium, coverageAmount, durationMonths, isActive
 *
 * RELATIONSHIPS:
 *   - ONE policy can be purchased by MANY users → see entity/UserPolicy.java
 *   - ONE policy has MANY premium calculation logs → see entity/PremiumCalculationLog.java
 *
 * FLOW:
 *   1. Admin creates a policy (e.g., "Gold Health Plan", basePremium=$200/month)
 *   2. User clicks "Preview Premium" → PremiumCalculationService calculates discounts
 *   3. User clicks "Purchase" → UserPolicy record is created with final premium
 *
 * USED BY:
 *   - PolicyController.java (controller/) → CRUD endpoints
 *   - PolicyService.java (service/) → business logic
 *   - PolicyRepository.java (repository/) → database queries
 *   - UserPolicyService.java (service/) → purchasing logic
 *   - PremiumCalculationService.java (service/) → premium preview
 *   - PolicyMgmtComponent (frontend: pages/admin/policy-mgmt/) → admin creates policies
 *   - PoliciesComponent (frontend: pages/policies/) → user browses & buys policies
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an insurance policy template available for purchase.
 * Admin creates policies; users can purchase them via UserPolicy.
 */
@Entity
@Table(name = "policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(nullable = false)
    private String title;  // e.g., "Gold Health Plan"

    @Column(columnDefinition = "TEXT")
    private String description;  // Detailed description of what's covered

    @Enumerated(EnumType.STRING) // Stored as "LIFE", "HEALTH", or "MOTOR" in DB
    @Column(nullable = false)
    private PolicyType policyType;

    /**
     * Base premium amount (in USD) BEFORE any gamification discounts.
     * This is the starting price. Discounts reduce this.
     * e.g., $200/month base, 15% discount → $170/month final
     */
    @Column(nullable = false)
    private Double basePremium;

    /**
     * Maximum coverage amount for this policy (in USD).
     * e.g., $500,000 coverage for a life insurance policy
     */
    @Column(nullable = false)
    private Double coverageAmount;

    /**
     * Duration of the policy in months.
     * e.g., 12 = 1 year policy
     */
    @Column(nullable = false)
    private Integer durationMonths;

    /**
     * Whether this policy is currently offered to users.
     * Admin can deactivate a policy without deleting it.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    private String ageRange; // e.g., "18-65"
    private String planType; // e.g., "INDIVIDUAL", "FAMILY", "SENIOR_CITIZEN"
    
    // Health-specific details for Premium IQsure look
    private String waitingPeriod; // e.g., "1 month", "3 months"
    private Boolean hasMaternityCover;
    private Boolean hasPreExistingCover;

    // ONE policy can be purchased by MANY users
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserPolicy> userPolicies = new ArrayList<>();  // → entity/UserPolicy.java

    // Premium calculation audit logs for this policy
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PremiumCalculationLog> premiumLogs = new ArrayList<>(); // → entity/PremiumCalculationLog.java

    // The three types of insurance policies offered
    public enum PolicyType {
        LIFE,    // Life insurance
        HEALTH,  // Health insurance
        MOTOR    // Car/vehicle insurance
    }
}

