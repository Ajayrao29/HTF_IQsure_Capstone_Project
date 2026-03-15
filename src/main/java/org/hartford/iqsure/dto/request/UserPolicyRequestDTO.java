/*
 * FILE: UserPolicyRequestDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO for purchasing a policy. User only sends the policyId.
 *          The server calculates the final premium automatically.
 * FLOW: PoliciesComponent → api.service.ts → POST /api/v1/users/{userId}/policies
 *       → UserPolicyController → UserPolicyService.purchasePolicy()
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request body for purchasing a policy.
 * The finalPremium is calculated server-side — user only provides policyId.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicyRequestDTO {

    @NotNull(message = "Policy ID is required")
    private Long policyId;

    private String nomineeName;
    private String nomineeRelationship;
    private String healthReportPath;
    private java.util.List<InsuredMemberRequestDTO> insuredMembers;
    private java.util.List<Long> rewardIds;
}
