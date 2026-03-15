package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.repository.ClaimRepository;
import org.hartford.iqsure.repository.UserPolicyRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final UserRepository userRepository;

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByUser(Long userId) {
        return claimRepository.findByUser_UserId(userId);
    }

    public List<Claim> getClaimsByStatus(Claim.ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
    }

    @Transactional
    public Claim fileClaim(Long userId, Long userPolicyId, Claim claimRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserPolicy userPolicy = userPolicyRepository.findById(userPolicyId).orElseThrow(() -> new RuntimeException("User Policy not found"));

        Claim claim = Claim.builder()
                .claimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .userPolicy(userPolicy)
                .type(claimRequest.getType())
                .amount(claimRequest.getAmount())
                .hospitalName(claimRequest.getHospitalName())
                .incidentDate(claimRequest.getIncidentDate())
                .diagnosis(claimRequest.getDiagnosis())
                .status(Claim.ClaimStatus.SUBMITTED)
                .build();

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim assignOfficer(Long claimId, Long officerId) {
        Claim claim = getClaimById(claimId);
        User officer = userRepository.findById(officerId).orElseThrow(() -> new RuntimeException("Officer not found"));

        if (officer.getRole() != User.Role.ROLE_CLAIMS_OFFICER) {
            throw new RuntimeException("User is not a claims officer");
        }

        claim.setAssignedOfficer(officer);
        claim.setStatus(Claim.ClaimStatus.UNDER_REVIEW);
        claim.setReviewStartedAt(LocalDateTime.now());

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim processClaim(Long claimId, Claim.ClaimStatus status, String remarks, java.math.BigDecimal approvedAmount) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(status);
        claim.setReviewerRemarks(remarks);
        claim.setReviewedAt(LocalDateTime.now());
        
        if (status == Claim.ClaimStatus.APPROVED || status == Claim.ClaimStatus.PARTIAL_APPROVED) {
            claim.setApprovedAmount(approvedAmount);
        }

        return claimRepository.save(claim);
    }
}
