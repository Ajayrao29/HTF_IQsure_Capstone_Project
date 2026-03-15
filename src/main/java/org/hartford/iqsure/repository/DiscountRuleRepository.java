/*
 * FILE: DiscountRuleRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "discount_rules" table. Used by DiscountRuleService.java, PremiumCalculationService.java.
 * ENTITY: DiscountRule.java (entity/)
 * KEY METHOD: findActiveRulesForPolicyType → gets rules that apply to a specific policy type or ALL types
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.DiscountRule;
import org.hartford.iqsure.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Long> {

    List<DiscountRule> findByIsActiveTrue();

    /**
     * Fetch all active rules that apply to the given policy type OR apply to ALL types (null).
     */
    @Query("SELECT dr FROM DiscountRule dr WHERE dr.isActive = true " +
           "AND (dr.applicablePolicyType IS NULL OR dr.applicablePolicyType = :policyType)")
    List<DiscountRule> findActiveRulesForPolicyType(@Param("policyType") Policy.PolicyType policyType);

    boolean existsByRuleName(String ruleName);
}
