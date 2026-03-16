package org.hartford.iqsure.dto.response;

import lombok.*;
import org.hartford.iqsure.entity.Claim;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseDTO {
    private Long id;
    private String claimNumber;
    private Long userId;
    private String userName;
    private Long userPolicyId;
    private String policyTitle;
    private Claim.ClaimType type;
    private BigDecimal amount;
    private BigDecimal approvedAmount;
    private BigDecimal settlementAmount;
    private String hospitalName;
    private LocalDate incidentDate;
    private String diagnosis;
    private Claim.ClaimStatus status;
    private String rejectionReason;
    
    // Officer details
    private Long assignedOfficerId;
    private String assignedOfficerName;
    private LocalDateTime reviewStartedAt;
    private LocalDateTime reviewedAt;
    private String reviewerRemarks;
    
    private LocalDate settlementDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClaimResponseDTO fromEntity(Claim claim) {
        if (claim == null) return null;
        return ClaimResponseDTO.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .userId(claim.getUser() != null ? claim.getUser().getUserId() : null)
                .userName(claim.getUser() != null ? claim.getUser().getName() : null)
                .userPolicyId(claim.getUserPolicy() != null ? claim.getUserPolicy().getId() : null)
                .policyTitle(claim.getUserPolicy() != null && claim.getUserPolicy().getPolicy() != null ? 
                             claim.getUserPolicy().getPolicy().getTitle() : null)
                .type(claim.getType())
                .amount(claim.getAmount())
                .approvedAmount(claim.getApprovedAmount())
                .settlementAmount(claim.getSettlementAmount())
                .hospitalName(claim.getHospitalName())
                .incidentDate(claim.getIncidentDate())
                .diagnosis(claim.getDiagnosis())
                .status(claim.getStatus())
                .rejectionReason(claim.getRejectionReason())
                .assignedOfficerId(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getUserId() : null)
                .assignedOfficerName(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getName() : null)
                .reviewStartedAt(claim.getReviewStartedAt())
                .reviewedAt(claim.getReviewedAt())
                .reviewerRemarks(claim.getReviewerRemarks())
                .settlementDate(claim.getSettlementDate())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
