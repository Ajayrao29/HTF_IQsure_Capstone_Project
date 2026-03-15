/*
 * FILE: BadgeService.java | LOCATION: service/
 * PURPOSE: Badge CRUD + automatic badge awarding logic.
 *          checkAndAwardBadges() is called after every first quiz attempt to see if user
 *          qualifies for new badges based on their total points.
 * CALLED BY: BadgeController.java, QuizAttemptService.java
 * USES: BadgeRepository, UserRepository, UserBadgeRepository
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.BadgeRequestDTO;
import org.hartford.iqsure.dto.response.BadgeResponseDTO;
import org.hartford.iqsure.entity.Badge;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserBadge;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.BadgeRepository;
import org.hartford.iqsure.repository.UserBadgeRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;

    public BadgeResponseDTO createBadge(BadgeRequestDTO dto) {
        Badge badge = Badge.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .reqPoints(dto.getReqPoints())
                .build();
        return toDTO(badgeRepository.save(badge));
    }

    public List<BadgeResponseDTO> getAllBadges() {
        return badgeRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<BadgeResponseDTO> getBadgesByUser(Long userId) {
        return userBadgeRepository.findByUser_UserId(userId)
                .stream().map(ub -> toDTO(ub.getBadge())).toList();
    }

    public void deleteBadge(Long badgeId) {
        if (!badgeRepository.existsById(badgeId)) {
            throw new ResourceNotFoundException("Badge not found with id: " + badgeId);
        }
        badgeRepository.deleteById(badgeId);
    }

    public BadgeResponseDTO updateBadge(Long badgeId, BadgeRequestDTO dto) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + badgeId));
        badge.setName(dto.getName());
        badge.setDescription(dto.getDescription());
        badge.setReqPoints(dto.getReqPoints());
        return toDTO(badgeRepository.save(badge));
    }

    // Called automatically after every first quiz attempt.
    // Awards all badges the user qualifies for but hasn't received yet.
    public List<BadgeResponseDTO> checkAndAwardBadges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        List<Badge> eligible = badgeRepository.findByReqPointsLessThanEqual(user.getUserPoints());
        List<BadgeResponseDTO> newlyAwarded = new ArrayList<>();

        for (Badge badge : eligible) {
            boolean alreadyHas = userBadgeRepository
                    .existsByUser_UserIdAndBadge_BadgeId(userId, badge.getBadgeId());
            if (!alreadyHas) {
                UserBadge ub = UserBadge.builder()
                        .user(user)
                        .badge(badge)
                        .earnedDate(LocalDateTime.now())
                        .build();
                userBadgeRepository.save(ub);
                newlyAwarded.add(toDTO(badge));
            }
        }

        return newlyAwarded;
    }

    private BadgeResponseDTO toDTO(Badge b) {
        return BadgeResponseDTO.builder()
                .badgeId(b.getBadgeId())
                .name(b.getName())
                .description(b.getDescription())
                .reqPoints(b.getReqPoints())
                .build();
    }
}
