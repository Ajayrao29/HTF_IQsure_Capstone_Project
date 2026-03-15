/*
 * FILE: RewardResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO for reward data sent to frontend. Maps to "Reward" interface in models/models.ts.
 * RETURNED BY: RewardController endpoints → RewardService.toDTO()
 * USED IN FRONTEND: RewardsComponent (pages/rewards/), RewardMgmtComponent (pages/admin/reward-mgmt/)
 */
package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RewardResponseDTO {

    private Long rewardId;
    private String rewardType;
    private Double discountValue;
    private LocalDate expiryDate;
}
