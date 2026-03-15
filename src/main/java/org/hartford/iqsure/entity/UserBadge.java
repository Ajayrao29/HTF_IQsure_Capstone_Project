/*
 * ============================================================================
 * FILE: UserBadge.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: JOIN TABLE between User and Badge.
 *          When a user earns a badge, a row is created here linking them.
 *          This is a Many-to-Many relationship broken into two Many-to-One.
 *
 * DATABASE TABLE: user_badges
 *   - id (primary key)
 *   - user_id (FK → users table)
 *   - badge_id (FK → badges table)
 *   - earnedDate (when the badge was awarded)
 *
 * USED BY:
 *   - BadgeService.java → checkAndAwardBadges() creates UserBadge records
 *   - UserBadgeRepository.java → queries to check if user has a badge
 *   - PremiumCalculationService.java → counts badges for discount calculation
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user earned this badge
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;    // → entity/User.java

    // Which badge was earned
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;  // → entity/Badge.java

    // When the badge was awarded
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime earnedDate = LocalDateTime.now();
}
