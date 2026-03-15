/*
 * FILE: RewardRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "rewards" table. Used by RewardService.java.
 * ENTITY: Reward.java (entity/)
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    // Get all rewards that are still valid (not expired)
    List<Reward> findByExpiryDateAfter(LocalDate today);

    // Get rewards by type (e.g. "PREMIUM_DISCOUNT", "CASHBACK")
    List<Reward> findByRewardType(String rewardType);
}
