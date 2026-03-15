/*
 * ============================================================================
 * FILE: UserReward.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: JOIN TABLE between User and Reward.
 *          When a user redeems a reward, a row is created here.
 *          Prevents duplicate redemptions (user can redeem each reward only once).
 *
 * DATABASE TABLE: user_rewards
 *   - id, user_id (FK), reward_id (FK), redeemedDate
 *
 * USED BY:
 *   - RewardService.java → redeemReward() creates UserReward records
 *   - UserRewardRepository.java → checks if user already redeemed
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user redeemed this reward
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;      // → entity/User.java

    // Which reward was redeemed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;  // → entity/Reward.java

    // When the reward was redeemed
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime redeemedDate = LocalDateTime.now();
    
    // Tracks if this reward has been applied to a policy purchase (one-time use)
    @Column(name = "is_used", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean used = false;
}
