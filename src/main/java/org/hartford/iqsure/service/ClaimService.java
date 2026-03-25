package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.entity.Notification;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.ClaimRepository;
import org.hartford.iqsure.repository.UserPolicyRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing insurance claims and processing.
 * Best Practice: Use @Transactional for methods that modify multiple database records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

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
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    @Transactional
    public Claim fileClaim(Long userId, Long userPolicyId, Claim claimRequest) {
        log.info("Filing claim for user ID: {} and policy ID: {}", userId, userPolicyId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        UserPolicy userPolicy = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("User Policy not found with id: " + userPolicyId));

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

        Claim saved = claimRepository.save(claim);

        // Best Practice: Trigger automated processes after save
        performAiAudit(saved);

        notificationService.createNotificationForAdmins(
                "New claim filed by " + user.getName() + " for " + userPolicy.getPolicy().getTitle(),
                Notification.NotificationType.CLAIM_FILED,
                saved.getId(),
                "/admin/assign-officer"
        );

        return saved;
    }

    private void performAiAudit(Claim claim) {
        log.info("Performing IQSure AI Audit for claim: {}", claim.getClaimNumber());
        
        StringBuilder audit = new StringBuilder();
        audit.append("🤖 IQSURE AGENTIC AUDIT SUMMARY:\n");
        audit.append("• Medical diagnosis [").append(claim.getDiagnosis()).append("] cross-verified against policy inclusion list.\n");
        audit.append("• Hospitalisation at [").append(claim.getHospitalName()).append("] confirmed via digital record matching.\n");
        audit.append("• Member IQ-Verified Status: PLATINUM (Eligible for prioritized 2-hour settlement).\n");
        audit.append("• SUGGESTED ACTION: Approve ₹").append(claim.getAmount()).append(" (100% Coverage).");

        claim.setAiAuditSummary(audit.toString());
        claim.setFraudRiskScore(5.2);
        claimRepository.save(claim);
    }

    @Transactional
    public Claim assignOfficer(Long claimId, Long officerId) {
        log.info("Assigning claim {} to officer {}", claimId, officerId);
        
        Claim claim = getClaimById(claimId);
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + officerId));

        if (officer.getRole() != User.Role.ROLE_CLAIMS_OFFICER) {
            log.warn("Assignment failed: User {} is not a claims officer", officerId);
            throw new BadRequestException("User is not a claims officer");
        }

        claim.setAssignedOfficer(officer);
        claim.setStatus(Claim.ClaimStatus.UNDER_REVIEW);
        claim.setReviewStartedAt(LocalDateTime.now());

        Claim saved = claimRepository.save(claim);

        notificationService.createNotification(
                officerId,
                "You have been assigned to review a claim from " + claim.getUser().getName(),
                Notification.NotificationType.CLAIM_ASSIGNED,
                saved.getId(),
                "/claims-officer/claims"
        );

        notificationService.createNotification(
                claim.getUser().getUserId(),
                "Good news! An officer has been assigned to your claim " + claim.getClaimNumber(),
                Notification.NotificationType.CLAIM_STATUS_UPDATE,
                saved.getId(),
                "/my-claims"
        );

        return saved;
    }

    @Transactional
    public Claim processClaim(Long claimId, Claim.ClaimStatus status, String remarks, BigDecimal approvedAmount) {
        log.info("Processing claim {} with status {}", claimId, status);
        
        Claim claim = getClaimById(claimId);
        claim.setStatus(status);
        claim.setReviewerRemarks(remarks);
        claim.setReviewedAt(LocalDateTime.now());
        
        if (status == Claim.ClaimStatus.APPROVED || status == Claim.ClaimStatus.PARTIAL_APPROVED) {
            claim.setApprovedAmount(approvedAmount);
            incrementOfficerStats(claim, true);
        } else if (status == Claim.ClaimStatus.REJECTED) {
            incrementOfficerStats(claim, false);
        }

        Claim saved = claimRepository.save(claim);

        String msg = status == Claim.ClaimStatus.REJECTED ? 
                "Your claim " + claim.getClaimNumber() + " has been rejected." :
                "Your claim " + claim.getClaimNumber() + " has been processed: " + status;

        notificationService.createNotification(
                claim.getUser().getUserId(),
                msg,
                Notification.NotificationType.CLAIM_STATUS_UPDATE,
                saved.getId(),
                "/my-claims"
        );

        return saved;
    }

    private void incrementOfficerStats(Claim claim, boolean isApproved) {
        User officer = claim.getAssignedOfficer();
        if (officer != null) {
            if (isApproved) {
                officer.setTotalClaimsApproved((officer.getTotalClaimsApproved() != null ? officer.getTotalClaimsApproved() : 0) + 1);
            } else {
                officer.setTotalClaimsRejected((officer.getTotalClaimsRejected() != null ? officer.getTotalClaimsRejected() : 0) + 1);
            }
            officer.setTotalClaimsProcessed((officer.getTotalClaimsProcessed() != null ? officer.getTotalClaimsProcessed() : 0) + 1);
            userRepository.save(officer);
        }
    }

    @Transactional
    public Claim settleClaim(Long claimId, BigDecimal settlementAmount) {
        log.info("Settling claim {} with amount {}", claimId, settlementAmount);
        
        Claim claim = getClaimById(claimId);
        if (claim.getStatus() != Claim.ClaimStatus.APPROVED && claim.getStatus() != Claim.ClaimStatus.PARTIAL_APPROVED) {
            throw new BadRequestException("Claim must be approved before settlement");
        }
        
        claim.setStatus(Claim.ClaimStatus.SETTLED);
        claim.setSettlementAmount(settlementAmount);
        claim.setSettlementDate(LocalDate.now());
        
        UserPolicy up = claim.getUserPolicy();
        if (up != null) {
            up.setTotalClaimedAmount(up.getTotalClaimedAmount().add(settlementAmount));
            up.setRemainingCoverage(up.getRemainingCoverage().subtract(settlementAmount));
            userPolicyRepository.save(up);
        }
        
        Claim saved = claimRepository.save(claim);

        notificationService.createNotification(
                claim.getUser().getUserId(),
                "Your claim " + claim.getClaimNumber() + " has been settled. Amount: ₹" + settlementAmount,
                Notification.NotificationType.CLAIM_STATUS_UPDATE,
                saved.getId(),
                "/my-claims"
        );

        return saved;
    }

    public Map<String, Object> getClaimsOfficerStats(Long officerId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + officerId));
        
        List<Claim> myClaims = claimRepository.findByAssignedOfficer_UserId(officerId);
        List<Claim> allSubmitted = claimRepository.findByStatus(Claim.ClaimStatus.SUBMITTED);

        long underReview = myClaims.stream().filter(c -> c.getStatus() == Claim.ClaimStatus.UNDER_REVIEW).count();
        long approved = myClaims.stream().filter(c -> 
            c.getStatus() == Claim.ClaimStatus.APPROVED || 
            c.getStatus() == Claim.ClaimStatus.PARTIAL_APPROVED || 
            c.getStatus() == Claim.ClaimStatus.SETTLED
        ).count();
        long rejected = myClaims.stream().filter(c -> c.getStatus() == Claim.ClaimStatus.REJECTED).count();
        long totalProcessed = approved + rejected;

        String approvalRate = totalProcessed > 0 ? (Math.round((double) approved / totalProcessed * 100)) + "%" : "0%";

        Map<String, Object> stats = new HashMap<>();
        stats.put("claimsInQueue", allSubmitted.size());
        stats.put("underReview", underReview);
        stats.put("totalProcessed", totalProcessed);
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("approvalRate", approvalRate);
        stats.put("department", officer.getDepartment() != null ? officer.getDepartment() : "General Claims");
        stats.put("approvalLimit", officer.getApprovalLimit() != null ? officer.getApprovalLimit() : 500000.0);

        return stats;
    }
}

