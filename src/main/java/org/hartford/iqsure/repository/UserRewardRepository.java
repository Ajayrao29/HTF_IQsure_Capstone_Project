/*
 * FILE: UserRewardRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "user_rewards" join table. Used by RewardService.java.
 * ENTITY: UserReward.java (entity/) — links users to their redeemed rewards
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {

    // All rewards redeemed by a user
    List<UserReward> findByUser_UserId(Long userId);

    // Only unused (not yet applied to a policy) redeemed rewards for a user
    // Field is 'used' in UserReward entity, so Spring Data method uses 'UsedFalse'
    List<UserReward> findByUser_UserIdAndUsedFalse(Long userId);

    // Check if user already redeemed a specific reward
    boolean existsByUser_UserIdAndReward_RewardId(Long userId, Long rewardId);
}
