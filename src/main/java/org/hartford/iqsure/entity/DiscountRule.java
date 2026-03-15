/*
 * ============================================================================
 * FILE: DiscountRule.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "discount_rules" table. Admin-configurable rules
 *          that determine how much discount a user gets on insurance premiums
 *          based on their gamification engagement (points, badges, quiz scores).
 *
 * DATABASE TABLE: discount_rules
 *   - ruleId, ruleName, description, minQuizScorePercent, minUserPoints,
 *     minBadgesEarned, discountPercentage, applicablePolicyType, isActive
 *
 * HOW DISCOUNT RULES WORK:
 *   1. Admin creates a rule: "Score 80%+ on quizzes → 10% discount on HEALTH policies"
 *   2. When user previews/purchases a policy, PremiumCalculationService checks ALL rules
 *   3. For each rule, it checks: does user meet ALL conditions? (points, badges, score)
 *   4. If yes → that rule's discount is added to total
 *   5. Total discount is capped at 50% (see AppConfig.java → maxDiscountCap)
 *
 * EXAMPLE RULE:
 *   - ruleName: "Health Quiz Expert"
 *   - minQuizScorePercent: 80.0 (user must score >= 80% on quizzes)
 *   - minUserPoints: 200 (user must have >= 200 points)
 *   - minBadgesEarned: 2 (user must have >= 2 badges)
 *   - discountPercentage: 10.0 (gives 10% off)
 *   - applicablePolicyType: HEALTH (only applies to health policies)
 *
 * USED BY:
 *   - DiscountRuleController.java → CRUD endpoints for admin
 *   - DiscountRuleService.java → business logic
 *   - PremiumCalculationService.java → evaluates rules during premium calculation
 *   - DiscountRulesComponent (frontend: pages/admin/discount-rules/) → admin UI
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Admin-configurable discount rules based on user gamification engagement.
 *
 * Each rule defines conditions (minimum quiz score, points, badges) and
 * a discount percentage to award when ALL conditions are met.
 *
 * The rule can optionally be scoped to a specific PolicyType or apply to ALL policies.
 */
@Entity
@Table(name = "discount_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @Column(nullable = false, unique = true)
    private String ruleName;  // Unique name like "Quiz Master Discount"

    @Column(columnDefinition = "TEXT")
    private String description;  // Human-readable description of this rule

    /**
     * Minimum quiz score percentage required (e.g., 80.0 means user must score >= 80%).
     * Set to 0 to ignore this condition (any score qualifies).
     */
    @Builder.Default
    @Column(nullable = false)
    private Double minQuizScorePercent = 0.0;

    /**
     * Minimum total user points required.
     * Set to 0 to ignore this condition.
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer minUserPoints = 0;

    /**
     * Minimum number of badges the user must have earned.
     * Set to 0 to ignore this condition.
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer minBadgesEarned = 0;

    /**
     * Discount percentage to apply when this rule's conditions are ALL met.
     * e.g., 10.0 means 10% off the base premium.
     */
    @Column(nullable = false)
    private Double discountPercentage;

    /**
     * Which policy type this rule applies to.
     * NULL means it applies to ALL policy types (LIFE, HEALTH, MOTOR).
     * → uses Policy.PolicyType enum from entity/Policy.java
     */
    @Enumerated(EnumType.STRING)
    private Policy.PolicyType applicablePolicyType;

    /**
     * Whether this rule is currently active.
     * Admin can disable rules without deleting them.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}
