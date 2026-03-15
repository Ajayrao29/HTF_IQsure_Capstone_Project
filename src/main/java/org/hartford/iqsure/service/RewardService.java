/*
 * FILE: RewardService.java | LOCATION: service/
 * PURPOSE: Reward management. Rewards are auto-granted when users qualify for discount rules.
 *          getEarnedRewardsForUser inlines the check-and-grant logic directly (avoids Spring
 *          proxy issue where @Transactional on internal method calls doesn't propagate).
 * CALLED BY: RewardController.java
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.RewardRequestDTO;
import org.hartford.iqsure.dto.response.RewardResponseDTO;
import org.hartford.iqsure.entity.*;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final DiscountRuleRepository discountRuleRepository;

    public RewardResponseDTO createReward(RewardRequestDTO dto) {
        Reward reward = Reward.builder()
                .rewardType(dto.getRewardType())
                .discountValue(dto.getDiscountValue())
                .expiryDate(dto.getExpiryDate())
                .build();
        return toDTO(rewardRepository.save(reward));
    }

    public List<RewardResponseDTO> getAllRewards() {
        return rewardRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<RewardResponseDTO> getActiveRewards() {
        return rewardRepository.findByExpiryDateAfter(LocalDate.now())
                .stream().map(this::toDTO).toList();
    }

    public List<RewardResponseDTO> getRewardsByUser(Long userId) {
        return userRewardRepository.findByUser_UserId(userId)
                .stream().map(ur -> toDTO(ur.getReward())).toList();
    }

    /**
     * Called when the Rewards page loads.
     * Inlines the check-and-grant logic (no internal method delegation)
     * so Spring @Transactional works correctly.
     */
    @Transactional
    public List<Map<String, Object>> getEarnedRewardsForUser(Long userId) {
        // ── Step 1: Load user stats ──────────────────────────────────────────
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        int userPoints = user.getUserPoints();
        int badgeCount = userBadgeRepository.findByUser_UserId(userId).size();
        double bestScore = quizAttemptRepository
                .findByUser_UserIdOrderByAttemptDateDesc(userId)
                .stream()
                .mapToDouble(a -> a.getTotalQuestions() > 0
                        ? ((double) a.getScore() / a.getTotalQuestions()) * 100.0 : 0.0)
                .max().orElse(0.0);

        System.out.println("[RewardGrant] userId=" + userId
                + " pts=" + userPoints + " badges=" + badgeCount + " bestScore=" + bestScore);

        // ── Step 2: Check every active discount rule ────────────────────────
        List<DiscountRule> rules = discountRuleRepository.findByIsActiveTrue();

        for (DiscountRule rule : rules) {
            if (rule.getDiscountPercentage() == null) continue;

            boolean qualifies =
                (rule.getMinUserPoints() <= 0       || userPoints >= rule.getMinUserPoints()) &&
                (rule.getMinQuizScorePercent() <= 0 || bestScore  >= rule.getMinQuizScorePercent()) &&
                (rule.getMinBadgesEarned() <= 0     || badgeCount >= rule.getMinBadgesEarned());

            System.out.println("[RewardGrant] Rule '" + rule.getRuleName()
                    + "' qualifies=" + qualifies
                    + " (needPts=" + rule.getMinUserPoints()
                    + " needScore=" + rule.getMinQuizScorePercent()
                    + " needBadges=" + rule.getMinBadgesEarned() + ")");

            if (!qualifies) continue;

            // ── Step 3: Grant the reward if not already owned ──────────────
            String label = "Discount: " + rule.getRuleName();

            // Re-fetch each iteration so we see newly saved records
            List<UserReward> currentOwned = userRewardRepository.findByUser_UserId(userId);
            boolean alreadyHas = currentOwned.stream()
                    .anyMatch(ur -> ur.getReward().getRewardType().equals(label));

            if (!alreadyHas) {
                Reward reward = rewardRepository.save(Reward.builder()
                        .rewardType(label)
                        .discountValue(rule.getDiscountPercentage())
                        .expiryDate(LocalDate.now().plusMonths(6))
                        .build());

                userRewardRepository.save(UserReward.builder()
                        .user(user)
                        .reward(reward)
                        .redeemedDate(LocalDateTime.now())
                        .build());

                System.out.println("[RewardGrant] ✅ Granted '" + label + "' to user " + userId);
            }
        }

        // ── Step 4: Return the full (now up-to-date) list ──────────────────
        return userRewardRepository.findByUser_UserId(userId)
                .stream()
                .map(ur -> {
                    Reward r = ur.getReward();
                    return Map.<String, Object>of(
                        "userRewardId",  ur.getId(),
                        "rewardType",    r.getRewardType(),
                        "discountValue", r.getDiscountValue(),
                        "expiryDate",    r.getExpiryDate().toString(),
                        "earnedOn",      ur.getRedeemedDate().toLocalDate().toString(),
                        "isUsed",        ur.isUsed(),
                        "isExpired",     r.getExpiryDate().isBefore(LocalDate.now())
                    );
                })
                .toList();
    }

    public RewardResponseDTO redeemReward(Long userId, Long rewardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found: " + rewardId));

        if (reward.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("This reward has expired");
        }

        if (userRewardRepository.existsByUser_UserIdAndReward_RewardId(userId, rewardId)) {
            throw new BadRequestException("You have already redeemed this reward");
        }

        UserReward userReward = UserReward.builder()
                .user(user)
                .reward(reward)
                .redeemedDate(LocalDateTime.now())
                .build();

        userRewardRepository.save(userReward);
        return toDTO(reward);
    }

    public void deleteReward(Long rewardId) {
        if (!rewardRepository.existsById(rewardId)) {
            throw new ResourceNotFoundException("Reward not found: " + rewardId);
        }
        rewardRepository.deleteById(rewardId);
    }

    private RewardResponseDTO toDTO(Reward r) {
        return RewardResponseDTO.builder()
                .rewardId(r.getRewardId())
                .rewardType(r.getRewardType())
                .discountValue(r.getDiscountValue())
                .expiryDate(r.getExpiryDate())
                .build();
    }
}
