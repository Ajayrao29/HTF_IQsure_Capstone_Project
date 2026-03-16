package org.hartford.iqsure.service;

import org.hartford.iqsure.config.AppConfig;
import org.hartford.iqsure.dto.response.PremiumBreakdownDTO;
import org.hartford.iqsure.entity.*;
import org.hartford.iqsure.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumCalculationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private UserBadgeRepository userBadgeRepository;
    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private UserRewardRepository userRewardRepository;
    @Mock
    private PremiumCalculationLogRepository logRepository;
    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private PremiumCalculationService premiumCalculationService;

    @Test
    public void testCalculatePremium_NoRewards() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).build();
        Policy policy = Policy.builder().policyId(1L).basePremium(1000.0).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(userBadgeRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());
        when(quizAttemptRepository.findBestScorePercentByUserId(1L)).thenReturn(0.0);
        when(appConfig.getMaxDiscountCap()).thenReturn(50.0);

        // --- ACT ---
        PremiumBreakdownDTO result = premiumCalculationService.calculatePremium(1L, 1L, Collections.emptyList());

        // --- ASSERT ---
        assertEquals(1000.0, result.getFinalPremium());
        assertEquals(0.0, result.getTotalDiscountPercent());
        verify(logRepository).save(any(PremiumCalculationLog.class));
    }

    @Test
    public void testCalculatePremium_WithReward() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).build();
        Policy policy = Policy.builder().policyId(1L).basePremium(1000.0).build();
        Reward reward = Reward.builder().rewardId(1L).rewardType("Coupon").discountValue(15.0).expiryDate(LocalDate.now().plusDays(10)).build();
        UserReward ur = UserReward.builder().id(10L).reward(reward).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(userRewardRepository.findByUser_UserIdAndUsedFalse(1L)).thenReturn(List.of(ur));
        when(appConfig.getMaxDiscountCap()).thenReturn(50.0);

        // --- ACT ---
        PremiumBreakdownDTO result = premiumCalculationService.calculatePremium(1L, 1L, List.of(10L));

        // --- ASSERT ---
        assertEquals(850.0, result.getFinalPremium());
        assertEquals(15.0, result.getTotalDiscountPercent());
    }
}
