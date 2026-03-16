package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.request.RewardRequestDTO;
import org.hartford.iqsure.dto.response.RewardResponseDTO;
import org.hartford.iqsure.service.RewardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RewardControllerTest {

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private RewardController rewardController;

    @Test
    public void testCreate() {
        // --- ARRANGE ---
        RewardRequestDTO request = new RewardRequestDTO();
        request.setRewardType("Bonus");
        RewardResponseDTO responseDTO = RewardResponseDTO.builder().rewardId(1L).rewardType("Bonus").build();
        when(rewardService.createReward(any(RewardRequestDTO.class))).thenReturn(responseDTO);

        // --- ACT ---
        ResponseEntity<RewardResponseDTO> response = rewardController.create(request);

        // --- ASSERT ---
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Bonus", response.getBody().getRewardType());
        verify(rewardService).createReward(any(RewardRequestDTO.class));
    }

    @Test
    public void testGetEarnedByUser() {
        // --- ARRANGE ---
        Map<String, Object> rewardMap = Map.of("rewardType", "Rule Reward");
        when(rewardService.getEarnedRewardsForUser(1L)).thenReturn(List.of(rewardMap));

        // --- ACT ---
        ResponseEntity<List<Map<String, Object>>> response = rewardController.getEarnedByUser(1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Rule Reward", response.getBody().get(0).get("rewardType"));
    }

    @Test
    public void testRedeem() {
        // --- ARRANGE ---
        RewardResponseDTO responseDTO = RewardResponseDTO.builder().rewardId(1L).build();
        when(rewardService.redeemReward(1L, 1L)).thenReturn(responseDTO);

        // --- ACT ---
        ResponseEntity<RewardResponseDTO> response = rewardController.redeem(1L, 1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        verify(rewardService).redeemReward(1L, 1L);
    }
}
