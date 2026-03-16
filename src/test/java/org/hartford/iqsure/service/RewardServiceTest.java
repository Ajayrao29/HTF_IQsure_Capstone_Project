package org.hartford.iqsure.service;

import org.hartford.iqsure.dto.request.RewardRequestDTO;
import org.hartford.iqsure.dto.response.RewardResponseDTO;
import org.hartford.iqsure.entity.Reward;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserReward;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRewardRepository userRewardRepository;
    @Mock
    private UserBadgeRepository userBadgeRepository;
    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private DiscountRuleRepository discountRuleRepository;

    @InjectMocks
    private RewardService rewardService;

    @Test
    public void testCreateReward() {
        // --- ARRANGE ---
        RewardRequestDTO dto = new RewardRequestDTO();
        dto.setRewardType("Cashback");
        dto.setDiscountValue(10.0);
        dto.setExpiryDate(LocalDate.now().plusMonths(1));

        Reward saved = Reward.builder().rewardId(1L).rewardType("Cashback").build();
        when(rewardRepository.save(any(Reward.class))).thenReturn(saved);

        // --- ACT ---
        RewardResponseDTO result = rewardService.createReward(dto);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("Cashback", result.getRewardType());
        verify(rewardRepository).save(any(Reward.class));
    }

    @Test
    public void testGetEarnedRewardsForUser_NoRulesMet() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).userPoints(10).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findByUser_UserId(1L)).thenReturn(List.of());
        when(quizAttemptRepository.findByUser_UserIdOrderByAttemptDateDesc(1L)).thenReturn(List.of());
        when(discountRuleRepository.findByIsActiveTrue()).thenReturn(List.of());

        // --- ACT ---
        List<Map<String, Object>> result = rewardService.getEarnedRewardsForUser(1L);

        // --- ASSERT ---
        assertTrue(result.isEmpty());
    }

    @Test
    public void testRedeemReward_Expired_ThrowsException() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).build();
        Reward expiredReward = Reward.builder().rewardId(1L).expiryDate(LocalDate.now().minusDays(1)).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(expiredReward));

        // --- ACT & ASSERT ---
        assertThrows(BadRequestException.class, () -> rewardService.redeemReward(1L, 1L));
    }
}
