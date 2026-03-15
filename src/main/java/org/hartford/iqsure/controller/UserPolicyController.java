/*
 * FILE: UserPolicyController.java | LOCATION: controller/
 * PURPOSE: Policy purchase and dynamic premium calculation API.
 *          Users can preview their personalized premium (with gamification discounts)
 *          and then purchase policies.
 * ENDPOINTS:
 *   POST /api/v1/users/{userId}/policies                    → Purchase a policy
 *   GET  /api/v1/users/{userId}/policies                    → User's purchased policies
 *   GET  /api/v1/users/{userId}/premium/calculate/{policyId}→ Preview premium before purchase
 *   GET  /api/v1/users/{userId}/premium/logs                → Premium calculation history
 * FLOW: PoliciesComponent / MyPoliciesComponent → api.service.ts → THIS → UserPolicyService / PremiumCalculationService
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.UserPolicyRequestDTO;
import org.hartford.iqsure.dto.response.PremiumBreakdownDTO;
import org.hartford.iqsure.dto.response.PremiumCalculationLogResponseDTO;
import org.hartford.iqsure.dto.response.UserPolicyResponseDTO;
import org.hartford.iqsure.service.PremiumCalculationService;
import org.hartford.iqsure.service.UserPolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;

/**
 * User Policy & Premium endpoints.
 *
 * POST /api/v1/users/{userId}/policies                            - purchase a policy
 * GET  /api/v1/users/{userId}/policies                            - get user's purchased policies
 * GET  /api/v1/users/{userId}/policies/{id}                       - get specific purchased policy
 * GET  /api/v1/users/{userId}/premium/calculate/{policyId}        - preview premium before purchase
 * GET  /api/v1/users/{userId}/premium/logs                        - all premium calculation history
 * GET  /api/v1/users/{userId}/premium/logs/{policyId}             - history for specific policy
 */
@RestController
@RequestMapping("/api/v1/users/{userId}")
@RequiredArgsConstructor
@Tag(name = "User Policies & Premium", description = "Policy purchase and dynamic premium calculation")
public class UserPolicyController {

    private final UserPolicyService userPolicyService;
    private final PremiumCalculationService premiumCalculationService;

    // ── Policy Purchase ───────────────────────────────────────────────────

    @PostMapping("/policies")
    @Operation(summary = "Purchase a policy (applies gamification discounts automatically)")
    public ResponseEntity<UserPolicyResponseDTO> purchasePolicy(
            @PathVariable Long userId,
            @Valid @RequestBody UserPolicyRequestDTO dto,
            @RequestParam(required = false) List<Long> queryRewardIds) {
        
        // Combine rewards from both sources if necessary
        List<Long> finalRewards = queryRewardIds != null ? new java.util.ArrayList<>(queryRewardIds) : new java.util.ArrayList<>();
        if (dto.getRewardIds() != null) {
            for (Long id : dto.getRewardIds()) {
                if (!finalRewards.contains(id)) finalRewards.add(id);
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userPolicyService.purchasePolicy(userId, dto, finalRewards));
    }

    @GetMapping("/policies")
    @Operation(summary = "Get all policies purchased by a user")
    public ResponseEntity<List<UserPolicyResponseDTO>> getUserPolicies(@PathVariable Long userId) {
        return ResponseEntity.ok(userPolicyService.getUserPolicies(userId));
    }

    @GetMapping("/policies/{userPolicyId}")
    @Operation(summary = "Get a specific purchased policy by ID")
    public ResponseEntity<UserPolicyResponseDTO> getUserPolicyById(
            @PathVariable Long userId,
            @PathVariable Long userPolicyId) {
        return ResponseEntity.ok(userPolicyService.getUserPolicyById(userId, userPolicyId));
    }

    // ── Premium Calculation ───────────────────────────────────────────────

    @GetMapping("/premium/calculate/{policyId}")
    @Operation(summary = "Preview dynamic premium for a policy before purchasing",
               description = "Evaluates all discount rules based on user's gamification data " +
                             "(points, badges, quiz scores and selected coupon rewards) and returns the full breakdown.")
    public ResponseEntity<PremiumBreakdownDTO> calculatePremium(
            @PathVariable Long userId,
            @PathVariable Long policyId,
            @RequestParam(required = false) List<Long> selectedRewardIds) {
        return ResponseEntity.ok(premiumCalculationService.calculatePremium(userId, policyId, selectedRewardIds));
    }

    @GetMapping("/premium/available-rewards")
    @Operation(summary = "Get user's available (unused) redeemed rewards for coupon selection")
    public ResponseEntity<List<java.util.Map<String, Object>>> getAvailableRewards(@PathVariable Long userId) {
        return ResponseEntity.ok(
            premiumCalculationService.getAvailableRewardsForUser(userId)
                .stream()
                .map(ur -> java.util.Map.<String, Object>of(
                        "userRewardId", ur.getId(),
                        "rewardType", ur.getReward().getRewardType(),
                        "discountValue", ur.getReward().getDiscountValue(),
                        "expiryDate", ur.getReward().getExpiryDate().toString()
                ))
                .toList()
        );
    }

    @GetMapping("/premium/logs")
    @Operation(summary = "Get all premium calculation history for a user")
    public ResponseEntity<List<PremiumCalculationLogResponseDTO>> getPremiumLogs(@PathVariable Long userId) {
        return ResponseEntity.ok(premiumCalculationService.getLogsForUser(userId));
    }

    @GetMapping("/premium/logs/{policyId}")
    @Operation(summary = "Get premium calculation history for a user on a specific policy")
    public ResponseEntity<List<PremiumCalculationLogResponseDTO>> getPremiumLogsByPolicy(
            @PathVariable Long userId,
            @PathVariable Long policyId) {
        return ResponseEntity.ok(premiumCalculationService.getLogsForUserAndPolicy(userId, policyId));
    }
}
