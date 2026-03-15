/*
 * FILE: RewardRequestDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO for creating a reward. Admin sends this.
 * FLOW: RewardMgmtComponent → api.service.ts → POST /api/v1/rewards → RewardController → RewardService
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RewardRequestDTO {

    @NotBlank(message = "Reward type is required")
    private String rewardType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private Double discountValue;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
}
