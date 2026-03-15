/*
 * FILE: UserBadgeRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "user_badges" join table. Used by BadgeService.java, PremiumCalculationService.java.
 * ENTITY: UserBadge.java (entity/) — links users to their earned badges
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    // All badges earned by a user
    List<UserBadge> findByUser_UserId(Long userId);

    // Check if user already has a specific badge (prevent duplicate awards)
    boolean existsByUser_UserIdAndBadge_BadgeId(Long userId, Long badgeId);
}
