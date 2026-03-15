/*
 * FILE: RewardController.java | LOCATION: controller/
 * PURPOSE: Reward management and redemption API. Admin creates rewards; users redeem them.
 * ENDPOINTS: POST/GET/DELETE /api/v1/rewards, POST /api/v1/rewards/{id}/redeem?userId=X
 * FLOW: RewardMgmtComponent / RewardsComponent → api.service.ts → THIS → RewardService
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.RewardRequestDTO;
import org.hartford.iqsure.dto.response.RewardResponseDTO;
import org.hartford.iqsure.service.RewardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "Reward management and redemption")
public class RewardController {

    private final RewardService rewardService;

    @PostMapping
    @Operation(summary = "Create a reward (Admin)")
    public ResponseEntity<RewardResponseDTO> create(@Valid @RequestBody RewardRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rewardService.createReward(dto));
    }

    @GetMapping
    @Operation(summary = "Get all rewards")
    public ResponseEntity<List<RewardResponseDTO>> getAll(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(rewardService.getActiveRewards());
        }
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get rewards redeemed by a user")
    public ResponseEntity<List<RewardResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getRewardsByUser(userId));
    }

    @GetMapping("/user/{userId}/earned")
    @Operation(summary = "Get all earned rewards for a user (auto-awarded from discount rules)")
    public ResponseEntity<List<java.util.Map<String, Object>>> getEarnedByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getEarnedRewardsForUser(userId));
    }

    @PostMapping("/{rewardId}/redeem")
    @Operation(summary = "Redeem a reward for a user")
    public ResponseEntity<RewardResponseDTO> redeem(
            @PathVariable Long rewardId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(rewardService.redeemReward(userId, rewardId));
    }

    @DeleteMapping("/{rewardId}")
    @Operation(summary = "Delete a reward (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long rewardId) {
        rewardService.deleteReward(rewardId);
        return ResponseEntity.noContent().build();
    }
}
