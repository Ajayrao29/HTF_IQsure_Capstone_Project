/*
 * ============================================================================
 * FILE: Badge.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "badges" table in the database.
 *          Badges are achievements users earn when they reach certain point levels.
 *          Example: "Bronze Learner" badge requires 100 points.
 *
 * DATABASE TABLE: badges
 *   - badgeId (primary key)
 *   - name (unique badge name, e.g., "Bronze Learner")
 *   - description (what this badge is about)
 *   - reqPoints (minimum points needed to earn this badge)
 *
 * HOW BADGES ARE AWARDED:
 *   1. User takes a quiz → earns points (QuizAttemptService.java)
 *   2. After scoring, BadgeService.checkAndAwardBadges() is called
 *   3. It checks: does the user's total points >= badge's reqPoints?
 *   4. If yes AND user doesn't already have it → award the badge
 *
 * RELATIONSHIPS:
 *   - ONE badge can be earned by MANY users → see entity/UserBadge.java
 *
 * USED BY:
 *   - BadgeController.java (controller/) → CRUD endpoints
 *   - BadgeService.java (service/) → logic to check & award badges
 *   - BadgeRepository.java (repository/) → database queries
 *   - BadgeMgmtComponent (frontend: pages/admin/badge-mgmt/) → admin creates badges
 *   - BadgesComponent (frontend: pages/badges/) → user sees their badges
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long badgeId;

    @Column(nullable = false, unique = true)  // Badge names must be unique
    private String name;  // e.g., "Bronze Learner", "Silver Scholar", "Gold Expert"

    @Column(columnDefinition = "TEXT")
    private String description;  // Description of what this badge represents

    /**
     * Minimum points required to earn this badge.
     * e.g. 100 points → "Bronze Learner"
     *      300 points → "Silver Scholar"
     * (see: BadgeService.java → checkAndAwardBadges() method)
     */
    @Column(nullable = false)
    private Integer reqPoints;

    @Column
    private String icon;

    // ONE badge can be earned by MANY users (through the UserBadge join table)
    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserBadge> userBadges = new ArrayList<>();  // → entity/UserBadge.java
}
