/*
 * ============================================================================
 * FILE: PremiumCalculationLog.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "premium_calculation_logs" table. An AUDIT LOG
 *          that records every premium calculation performed. Stores a snapshot
 *          of the user's engagement data and which discount rules were applied.
 *
 * WHY THIS EXISTS:
 *   - User points/badges can change over time
 *   - This log captures the EXACT state at the time of calculation
 *   - Useful for auditing: "Why did this user get 15% discount on this policy?"
 *
 * DATABASE TABLE: premium_calculation_logs
 *   - logId, user_id (FK), policy_id (FK), basePremium, totalDiscountPercent,
 *     finalPremium, userPointsSnapshot, badgeCountSnapshot, bestQuizScoreSnapshot,
 *     appliedRuleNames, calculatedAt
 *
 * CREATED BY:
 *   - PremiumCalculationService.java → calculatePremium() method saves a log
 *     every time a premium is calculated (both preview and purchase)
 *
 * USED BY:
 *   - UserPolicyController.java → GET endpoints to view calculation history
 *   - PremiumCalculationLogRepository.java → database queries
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Audit log for every premium calculation performed.
 * Stores user engagement snapshot and which discount rules were applied.
 */
@Entity
@Table(name = "premium_calculation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumCalculationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    /**
     * User for whom premium was calculated.
     * → entity/User.java
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Policy for which premium was calculated.
     * → entity/Policy.java
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    /**
     * Base premium of the policy before discounts.
     */
    @Column(nullable = false)
    private Double basePremium;

    /**
     * Total discount percentage applied (sum of all matching rules, capped at 50%).
     */
    @Column(nullable = false)
    private Double totalDiscountPercent;

    /**
     * Final premium after applying discounts.
     * finalPremium = basePremium - (basePremium * totalDiscountPercent / 100)
     */
    @Column(nullable = false)
    private Double finalPremium;

    /**
     * Snapshot of user's total points AT THE TIME of calculation.
     * User's points may change later, but this captures the exact value used.
     */
    @Column(nullable = false)
    private Integer userPointsSnapshot;

    /**
     * Snapshot of how many badges user had AT THE TIME of calculation.
     */
    @Column(nullable = false)
    private Integer badgeCountSnapshot;

    /**
     * Snapshot of best quiz score percentage AT THE TIME of calculation.
     */
    @Column(nullable = false)
    private Double bestQuizScoreSnapshot;

    /**
     * Comma-separated list of rule names that were applied.
     * e.g., "Quiz Master Discount, Badge Collector Bonus"
     * Or "None" if no rules matched.
     */
    @Column(columnDefinition = "TEXT")
    private String appliedRuleNames;

    // When this calculation was performed
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime calculatedAt = LocalDateTime.now();
}
