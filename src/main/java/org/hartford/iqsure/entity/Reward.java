/*
 * ============================================================================
 * FILE: Reward.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "rewards" table. Rewards are prizes (like premium
 *          discounts or cashback) that users can redeem.
 *
 * DATABASE TABLE: rewards
 *   - rewardId, rewardType, discountValue, expiryDate
 *
 * RELATIONSHIPS:
 *   - ONE reward can be redeemed by MANY users → see entity/UserReward.java
 *
 * USED BY:
 *   - RewardController.java (controller/) → CRUD + redeem endpoints
 *   - RewardService.java (service/) → business logic for redeeming
 *   - RewardRepository.java (repository/) → database queries
 *   - RewardMgmtComponent (frontend: pages/admin/reward-mgmt/) → admin UI
 *   - RewardsComponent (frontend: pages/rewards/) → user redeems rewards
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardId;

    @Column(nullable = false)
    private String rewardType;  // e.g., "PREMIUM_DISCOUNT", "CASHBACK"

    /**
     * Percentage or flat discount value.
     * e.g. 10.0 → 10% off a premium
     */
    @Column(nullable = false)
    private Double discountValue;

    @Column
    private String description;

    @Column
    private Integer reqPoints;

    @Column(nullable = false)
    private LocalDate expiryDate;  // Reward expires after this date (can't be redeemed)

    // ONE reward can be redeemed by MANY users
    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserReward> userRewards = new ArrayList<>();  // → entity/UserReward.java
}
